import React, { useState, useEffect } from 'react';
import {
  AccordionTrigger,
  AccordionContent,
  AccordionItem,
  Accordion,
} from "@/components/molecule/accordion";
import { Input } from "@/components/atom/input";
import { Checkbox } from "@/components/atom/checkbox";
import { Label } from "@/components/atom/label";
import { OptionDTO } from "../../../models";

interface ProductFiltersProps {
  options: OptionDTO[] | undefined; // Accept options as a prop
  onFilterChange: (filters: any) => void;
  resetTrigger: boolean;
}

const priceRanges = [
  { label: '0 - 200,000', min: 0, max: 200000 },
  { label: '200,000 - 500,000', min: 200000, max: 500000 },
  { label: '500,000 - 1,000,000', min: 500000, max: 1000000 },
  { label: '1,000,000+', min: 1000000, max: Infinity },
];

const ProductFilters: React.FC<ProductFiltersProps> = ({ options, onFilterChange, resetTrigger}) => {
  const [selectedOptionVariations, setSelectedOptionVariations] = useState<number[]>([]); // Track selected option variations
  const [selectedPriceRanges, setSelectedPriceRanges] = useState<{ min: number; max: number }[]>([]); // Track selected price ranges

  const handlePriceRangeSelect = (min: number, max: number) => {
    setSelectedPriceRanges(prev => {
      const isSelected = prev.some(range => range.min === min && range.max === max);
      if (isSelected) {
        return prev.filter(range => range.min !== min || range.max !== max); // Remove if already selected
      } else {
        return [...prev, { min, max }]; // Add new range
      }
    });
  };

  const handleOptionChange = (optionVariationId: number, isChecked: boolean) => {
    setSelectedOptionVariations(prev => {
      const updatedVariations = isChecked 
          ? [...prev, optionVariationId] 
          : prev.filter(id => id !== optionVariationId);
      
      return updatedVariations;
    });
  };

  React.useEffect(() => {
    onFilterChange({ 
      selectedOptionVariations,
      priceRanges: selectedPriceRanges // Include selected price ranges in the filter change
    });
  }, [selectedOptionVariations, selectedPriceRanges]); 

  useEffect(() => {
    setSelectedOptionVariations([]);
    setSelectedPriceRanges([]);
  }, [resetTrigger]);


  return (
      <div className="space-y-6">
        {/* Option Filters */}
        {options?.map(option => (
          <Accordion key={option.optionId} collapsible type="single">
            <AccordionItem value={`option-${option.optionId}`}>
              <AccordionTrigger className="text-base font-medium">
                {option.optionValue}
              </AccordionTrigger>
              <AccordionContent>
                <div className="grid gap-2">
                  {option.optionVariations?.map(variation => (
                    <Label key={variation.optionVariationId} className="flex items-center gap-2 font-normal">
                      <Checkbox
                        id={`option-${option.optionId}-variation-${variation.optionVariationId}`} 
                        checked={selectedOptionVariations.includes(variation.optionVariationId!)}
                        onCheckedChange={(checked: boolean) => 
                          handleOptionChange(variation.optionVariationId!, checked)
                        }
                      />
                      {variation.optionVariationValue}
                    </Label>
                  ))}
                </div>
              </AccordionContent>
            </AccordionItem>
          </Accordion>
        ))}

        {/* price filter */}
        <Accordion collapsible type="single">
          <AccordionItem value="price">
            <AccordionTrigger className="text-base font-medium">
              Price
            </AccordionTrigger>
            <AccordionContent>
              <div className="grid gap-4">
                {priceRanges.map((range, index) => (
                  <Label key={index} className="flex items-center gap-2 font-normal">
                    <Checkbox
                      checked={selectedPriceRanges.some(selected => selected.min === range.min && selected.max === range.max)} // Check if this range is selected
                      onCheckedChange={(checked: boolean) => 
                        handlePriceRangeSelect(range.min, range.max)
                      }
                    />
                    {range.label}
                  </Label>
                ))}
              </div>
            </AccordionContent>
          </AccordionItem>
        </Accordion>
      </div>
  );
};

export default ProductFilters;
