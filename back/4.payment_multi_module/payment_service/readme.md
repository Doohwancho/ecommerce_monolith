# 결제 모듈 처리 요구사항 및 대응 방안

본 문서는 결제 요청 시 발생할 수 있는 모든 시나리오를 정의하고, 각 상황에 대한 시스템의 대응 방안을 명시하여 결제 처리의 안정성과 일관성을 보장하는 것을 목적으로 한다.

1. success
    1. immediate success
2. failure 
   1. 명시적 실패 w/ 실패 메시지
   2. 일시적 실패 
   3. timeout 
   4. 중복 결제 요청 (Idempotency)
   5. 결제 중 취소 (User Cancellation)

# flowchart 
## a. 결제 flowchart 
```mermaid
flowchart TD
    subgraph "API Layer"
        A[API: POST /payments/process]
        A_Cancel["API: POST /payments/{id}/cancel"]
    end

    subgraph "Payment Service Logic"
        %% Idempotency Check (멱등성 체크)
        A --> B{DB: findByOrderId};
        B -- 없음(New Payment) --> F[1\. DB: Payment 생성<br>status: PROCESSING];
        B -- 있음(Existing Payment) --> C{기존 결제 상태?};
        C -- COMPLETED --> D[기존 성공 결과 반환];
        C -- PROCESSING/PENDING --> E[현재 처리 중 알림 반환];
        C -- FAILED/CANCELLED/UNKNOWN --> F;

        %% Payment Execution (결제 실행)
        F --> G[2\. PG사에 결제 요청];
        G --> H{PG 응답?};

        %% Success Path (성공)
        H -- 성공 (Success) --> I[3a. DB: status 'COMPLETED'로 변경];
        I --> I_Event[4a. 이벤트 발행  - PaymentSuccess];
        I_Event --> I_API[5a. API 성공 응답];

        %% Failure Paths (실패)
        H -- 명시적 실패 (Definitive Failure) --> L[3b. DB: status 'FAILED'로 변경<br>실패 사유 기록];
        L --> L_Event[4b. 이벤트 발행 - PaymentFailed];
        L_Event --> L_API[5b. API 실패 응답];
        
        %% Transient Failure Path (일시적 실패 - 재시도)
        H -- 일시적 실패 (Transient) --> O{재시도 횟수?};
        O -- 최대 횟수 미만 --> G;
        O -- 최대 횟수 도달 --> L;

        %% Timeout Path (타임아웃)
        H -- 타임아웃 (Timeout) --> P[3c. DB: status 'UNKNOWN'으로 변경];
        P --> P_API[5c. API 처리 중 응답];
        
        %% Cancellation Path (결제 취소)
        A_Cancel --> W{DB: findByOrderId};
        W -- 없음 --> W_Fail[API: 404 Not Found 응답];
        W -- 있음 --> X{결제 상태?};
        X -- PROCESSING/PENDING --> Y[DB: status 'CANCELLED'로 변경];
        Y --> Y_Event[이벤트 발행 - PaymentCancelled];
        Y_Event --> Y_API[API: 취소 성공 응답];
        X -- COMPLETED --> X_Fail[API: 환불 필요 에러 응답];
    end

    subgraph "Scheduler (Background Job)"
        %% Reconciliation Job (상태 동기화 스케줄러)
        R[Scheduler: 5분마다 실행] --> S{DB: 5분 경과한<br>'UNKNOWN' 건 조회};
        S -- 있음 --> T[PG사에 상태 조회 요청];
        T --> U{조회 결과?};
        U -- PG 응답: 성공 --> I;
        U -- PG 응답: 실패 --> L;
    end

    %% Styling
    style A fill:#9f9,stroke:#333,stroke-width:2px
    style A_Cancel fill:#9f9,stroke:#333,stroke-width:2px
    style R fill:#f9f,stroke:#333,stroke-width:2px
```

## b. 환불 flowchart
```mermaid
flowchart TD
    subgraph "API Layer & Sync Logic (즉시 응답)"
        A["API: POST /payments/{orderId}/refund<br>Body: { amount, reason }"]
        A --> B{DB: findByOrderId};
        B -- 없음 --> C[API: 404 Not Found 응답];
        B -- 있음 --> D{원본 결제 상태 'COMPLETED'?};
        D -- 아니오 --> E["API: 400 Bad Request 응답<br>(환불 불가 상태)"];
        D -- 예 --> F{환불 금액 유효한가?<br>원본 금액 이하};
        F -- 아니오 --> G["API: 400 Bad Request 응답<br>(원본 금액 초과)"];
        F -- 예 --> H["DB: 'refunds' 테이블에 데이터 생성<br>status: REFUND_REQUESTED"];
        H --> I["API: 202 Accepted 응답<br>('환불 요청이 접수되었습니다')"];
    end

    subgraph "Scheduler & Async Logic (백그라운드 처리)"
        J[Scheduler: 1분마다 실행] --> K{"DB: 'REFUND_REQUESTED'<br>상태인 환불 건 조회"};
        K -- 처리할 건 없음 --> K_End(( ))
        K -- 처리할 건 있음 --> L["DB: status<br>'REFUND_PROCESSING'으로 변경"];
        L --> M[PG사에 환불 요청];
        M --> N{PG 응답?};
        
        N -- 성공 --> O["DB: status 'REFUND_COMPLETED'<br>처리 시각 기록"];
        N -- 실패 --> P["DB: status 'REFUND_FAILED'<br>처리 시각 기록"];

        O --> Q[이벤트 발행 - RefundSuccess<br>-> 사용자에게 환불 완료 알림];
        P --> R[이벤트 발행 - RefundFailed<br>-> 운영팀에 실패 알림];
    end
    
    %% Styling
    style A fill:#9cf,stroke:#333,stroke-width:2px
    style J fill:#f9f,stroke:#333,stroke-width:2px
    style I fill:#9f9,stroke:#333,stroke-width:1px
```


## 1. 성공 케이스 (Happy Path)

### 1.1. 즉시 성공 (Immediate Success)

-   **상황**: PG사에 결제 요청을 보낸 후, 즉시 '성공' 응답을 받은 경우.
-   **요구사항**: 결제 상태를 '완료(COMPLETED)'로 즉시 변경하고, 사용자에게 결제가 완료되었음을 알려야 한다.
-   **대응 방안**:
    1.  `Payment` 테이블에서 해당 결제 건의 상태를 `COMPLETED`로 업데이트한다.
    2.  결제 완료 시각(`updatedAt`)을 기록한다.
    3.  주문 서비스(Order Service)에 결제 완료 이벤트를 발행(publish)하여 후속 처리(예: 배송 시작)를 위임한다.
    4.  사용자에게 결제 완료 알림(이메일, SMS 등)을 보낸다.

## 2. 실패 케이스 (Failure Cases)

### 2.1. 명시적 실패 (Definitive Failure)

-   **상황**: PG사로부터 '실패' 응답을 명확하게 받은 경우. (예: 한도 초과, 잔액 부족, 유효하지 않은 카드 정보, 사용자 취소 등)
-   **요구사항**: 결제 상태를 '실패(FAILED)'로 변경하고, 실패 원인을 기록해야 한다. 사용자에게는 명확한 실패 사유를 안내해야 한다. **이 경우는 재시도해서는 안 된다.**
-   **대응 방안**:
    1.  `Payment` 테이블의 상태를 `FAILED`로 업데이트한다.
    2.  PG가 제공한 실패 코드와 메시지를 별도 컬럼에 기록하여 원인 분석이 가능하도록 한다.
    3.  주문 서비스에 결제 실패 이벤트를 발행한다.
    4.  사용자에게 "잔액이 부족합니다." 와 같이 이해하기 쉬운 메시지를 보여주고, 재결제를 유도한다.

### 2.2. 일시적 실패 (Transient Failure)

-   **상황**: PG사 시스템의 일시적인 오류, 연동된 은행 시스템의 간헐적 장애 등으로 인해 일시적으로 '실패' 응답을 받은 경우.
-   **요구사항**: 시스템은 자동으로 제한된 횟수만큼 재시도를 수행해야 한다. 모든 재시도 후에도 실패 시, 최종적으로 '실패' 처리해야 한다.
-   **대응 방안**:
    1.  `Payment` 테이블의 `attemptCount`(시도 횟수)를 1 증가시킨다.
    2.  **Exponential Backoff** 전략을 사용하여 재시도 간격을 점차 늘려가며 최대 2~3회 재시도한다. (예: 1초, 2초, 4초 후 재시도)
    3.  최종 재시도까지 실패하면, **2.1. 명시적 실패**와 동일하게 `FAILED` 상태로 처리한다.
    4.  재시도 중에는 `Payment`의 상태를 `PROCESSING`으로 유지한다.

### 2.3. 응답 시간 초과 (Timeout / Unknown State)

-   **상황**: PG사에 결제 요청을 보냈으나, 정해진 시간(예: 30초) 내에 아무런 응답도 받지 못한 경우. **사용자가 실제로 돈을 지불했을 수도, 아닐 수도 있는 가장 위험한 상태.**
-   **요구사항**: 결제 상태를 임의로 '성공' 또는 '실패' 처리해서는 안 된다. 상태를 '처리 중(PROCESSING)' 또는 '확인 필요(UNKNOWN)'로 유지하고, 반드시 별도의 프로세스를 통해 실제 결제 결과를 확인해야 한다. **절대 요청을 재시도해서는 안 된다.**
-   **대응 방안**:
    1.  `Payment`의 상태를 `PROCESSING`으로 유지한다.
    2.  사용자에게는 "결제 결과를 확인 중입니다. 잠시 후 다시 확인해주세요." 와 같이 안내한다.
    3.  **결제 상태 확인 스케줄러(Reconciliation Job)** 를 통해 일정 시간 간격(예: 5분, 10분, 30분 후)으로 PG사에 해당 거래(`paymentKey` 또는 `orderId`)의 상태를 조회하는 API를 호출한다.
    4.  상태 조회 API를 통해 확인된 최종 결과(성공 또는 실패)를 바탕으로 `Payment`의 상태를 업데이트한다.

## 3. 시스템 및 엣지 케이스 (System & Edge Cases)

### 3.1. 중복 결제 요청 (Idempotency)

-   **상황**: 네트워크 문제나 사용자 실수(예: '결제하기' 버튼 더블 클릭)로 인해 동일한 `orderId`에 대한 결제 요청이 짧은 시간 내에 여러 번 들어온 경우.
-   **요구사항**: 시스템은 동일 주문에 대해 결제가 중복으로 실행되는 것을 방지해야 한다. 이를 위해 **멱등성**을 보장해야 한다.
-   **대응 방안**:
    1.  결제 요청을 받으면 `orderId`를 기준으로 `Payment` 테이블을 조회한다.
    2.  이미 `COMPLETED` 상태인 결제 건이 존재하면, 새로운 결제를 진행하지 않고 기존의 성공 결과를 반환한다.
    3.  `PENDING` 또는 `PROCESSING` 상태인 결제 건이 존재하면, 새로운 결제를 생성하지 않고 현재 진행 중인 결제의 상태를 조회하여 반환한다. (Locking 메커니즘 고려)

### 3.2. 결제 중 취소 (User Cancellation)

-   **상황**: 사용자가 결제를 진행하던 중(예: PG사 결제창) 이탈하거나 '취소' 버튼을 누른 경우.
-   **요구사항**: 결제 상태를 '취소(CANCELLED)'로 변경하고, 재고 등 관련 리소스를 원상 복구해야 한다.
-   **대응 방안**:
    1.  PG사가 제공하는 사용자 취소 콜백(Callback) URL을 통해 요청을 수신한다.
    2.  `Payment`의 상태를 `CANCELLED`로 업데이트한다.
    3.  주문 서비스에 결제 취소 이벤트를 발행하여 주문 취소 및 재고 복구 로직을 수행하도록 한다.
