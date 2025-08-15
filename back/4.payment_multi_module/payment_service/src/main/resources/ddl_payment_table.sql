CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id VARCHAR(255) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    payment_status VARCHAR(50) NOT NULL,
    failure_code VARCHAR(50),
    failure_message VARCHAR(255),
    created_at TIMESTAMP
);

CREATE TABLE refunds (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id BIGINT NOT NULL,                  -- 원본 결제(payments) 테이블의 ID
    refund_key VARCHAR(255) NOT NULL UNIQUE,     -- 환불 요청 고유 키
    amount DECIMAL(19, 2) NOT NULL,              -- 환불 금액
    status VARCHAR(50) NOT NULL,                 -- 환불 상태 (REFUND_REQUESTED, COMPLETED 등)
    reason VARCHAR(255),                         -- 환불 사유
    created_at TIMESTAMP NOT NULL,               -- 환불 요청 시각
    processed_at TIMESTAMP,                      -- 환불 처리 완료 시각
    FOREIGN KEY (payment_id) REFERENCES payments(id) -- payments 테이블과 연결
);