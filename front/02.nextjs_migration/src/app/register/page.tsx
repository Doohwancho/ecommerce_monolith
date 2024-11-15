'use client';

import { useState } from 'react';
import { ProgressBar, useFunnel } from '@/components/molecule/Funnel';
import { Button } from "@/components/atom/button";
import { BasicInfoStep } from './steps/BasicInfoStep';
import { AddressStep } from './steps/AddressStep';
import { CompletionStep } from './steps/CompletionStep';
import { RegisterRequestDTO, RegisterResponseDTO } from '../../../models';
import { BasicInfoFormData, AddressFormData } from './schemas';

import HeaderMainFooterLayout from "@/components/template/headerMainFooterLayout";
import Footer from "@/components/organism/footer";


export default function RegisterPage() {
  const steps = ['기본정보', '주소입력', '완료'] as const;
  const [Funnel, setStep, goBack, canGoBack] = useFunnel(steps, '기본정보');
  const [error, setError] = useState<string | null>(null);
  const [formData, setFormData] = useState<Partial<RegisterRequestDTO>>({});

  const onBasicInfoSubmit = async (values: BasicInfoFormData) => {
    setFormData({ ...formData, ...values });
    setStep('주소입력');
  };

  const onAddressSubmit = async (values: AddressFormData) => {
    try {
      const registerData: RegisterRequestDTO = {
        ...formData as any,
        address: values
      };

      const BASE_URL = process.env.NEXT_PUBLIC_API_URL;
      const response = await fetch(`${BASE_URL}/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(registerData),
      });

      if (!response.ok) {
        throw new Error('Registration failed');
      }

      const data: RegisterResponseDTO = await response.json();
      console.log(data.message);
      
      setStep('완료');
    } catch (err) {
      setError('Registration failed. Please try again.');
      console.error(err);
    }
  };

  return (
    <>
      <HeaderMainFooterLayout
        main={
          <div className="max-w-2xl mx-auto p-6 pb-48">
            <ProgressBar steps={steps} currentStep={Funnel.getCurrentStep()} />
            
            <div className="mt-8">
              {canGoBack && Funnel.getCurrentStep() !== '완료' && (
                <Button
                  variant="outline"
                  onClick={goBack}
                  className="mb-4"
                >
                  이전으로
                </Button>
              )}
              
              <Funnel>
                <Funnel.Step name="기본정보">
                  <BasicInfoStep onSubmit={onBasicInfoSubmit} />
                </Funnel.Step>

                <Funnel.Step name="주소입력">
                  <AddressStep onSubmit={onAddressSubmit} error={error} />
                </Funnel.Step>

                <Funnel.Step name="완료">
                  <CompletionStep />
                </Funnel.Step>
              </Funnel>
            </div>
          </div>
        }
        footer={<Footer />}
      />
    </>
  );
}