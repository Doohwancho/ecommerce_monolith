'use client';

import React, { Children, ReactNode, isValidElement, useState } from 'react';

interface FunnelProps<T extends readonly string[]> {
    step: T[number];
    children: ReactNode;
}

interface StepProps<T extends readonly string[]> {
    name: T[number];
    children: ReactNode;
}

interface ProgressBarProps {
    steps: readonly string[];
    currentStep: string;
}

const Funnel = <T extends readonly string[]>({ step, children }: FunnelProps<T>) => {
    const validElements = Children.toArray(children).filter(isValidElement);
    const targetElement = validElements.find(
        (child) => (child.props as StepProps<T>)?.name === step
    );

    if (!targetElement) {
        console.debug("<Step /> given inside <Funnel /> is not part of step-plans.");
        return null;
    }

    return <>{targetElement}</>;
};

const Step = <T extends readonly string[]>({ children }: StepProps<T>) => {
    return <>{children}</>;
};

export const useFunnel = <T extends readonly string[]>(
    steps: T,
    defaultStep: T[number]
) => {
    const [step, setStep] = useState<T[number]>(defaultStep);
    const [history, setHistory] = useState<T[number][]>([defaultStep]);

    const canGoBack = history.length > 1;

    const setStepWithHistory = (newStep: T[number]) => {
        setHistory((prev) => [...prev, newStep]);
        setStep(newStep);
    };

    const goBack = () => {
        if (canGoBack) {
            const newHistory = history.slice(0, -1);
            setHistory(newHistory);
            setStep(newHistory[newHistory.length - 1]);
        }
    };

    const FunnelElement = Object.assign(
        (props: Omit<FunnelProps<T>, 'step'>) => {
            return <Funnel step={step} {...props} />;
        },
        {
            Step: (props: StepProps<T>) => <Step<T> {...props} />,
            getCurrentStep: () => step
        }
    );

    return [FunnelElement, setStepWithHistory, goBack, canGoBack] as const;
};

export function ProgressBar({ steps, currentStep }: ProgressBarProps) {
    const currentIndex = steps.indexOf(currentStep);

    return (
        <div className="flex items-center justify-between w-full my-5">
            {steps.map((step, index) => (
                <React.Fragment key={step}>
                    <div
                        className={`w-8 h-8 rounded-full flex items-center justify-center ${
                            index <= currentIndex
                                ? 'bg-primary/80 text-white'
                                : 'bg-gray-200 text-black'
                        }`}
                    >
                        {index + 1}
                    </div>
                    {index < steps.length - 1 && (
                        <div
                            className={`flex-1 h-1 ${
                                index < currentIndex ? 'bg-primary/80' : 'bg-gray-200'
                            }`}
                        />
                    )}
                </React.Fragment>
            ))}
        </div>
    );
}