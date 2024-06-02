import {
  AccordionTrigger,
  AccordionContent,
  AccordionItem,
  Accordion,
} from "@/components/molecule/accordion";
import { Input } from "@/components/atom/input";
import { Checkbox } from "@/components/atom/checkbox";
import { Label } from "@/components/atom/label";

const ProductFilters = () => {
  return (
      <div className="space-y-6">
        {/* accordian2 - category */}
        <Accordion collapsible type="single">
          <AccordionItem value="category">
            <AccordionTrigger className="text-base font-medium">
              Category
            </AccordionTrigger>
            <AccordionContent>
              <div className="grid gap-2">
                <Label className="flex items-center gap-2 font-normal">
                  <Checkbox id="category-shoes" />
                  Shoes{"\n                                    "}
                </Label>
                <Label className="flex items-center gap-2 font-normal">
                  <Checkbox id="category-tops" />
                  Tops & T-Shirts{"\n                                    "}
                </Label>
                <Label className="flex items-center gap-2 font-normal">
                  <Checkbox id="category-hoodies" />
                  Hoodies & Pullovers{"\n                                    "}
                </Label>
                <Label className="flex items-center gap-2 font-normal">
                  <Checkbox id="category-tracksuits" />
                  Tracksuits{"\n                                    "}
                </Label>
                <Label className="flex items-center gap-2 font-normal">
                  <Checkbox id="category-socks" />
                  Socks{"\n                                    "}
                </Label>
              </div>
            </AccordionContent>
          </AccordionItem>
        </Accordion>

        {/* accordian3 - color */}
        <Accordion collapsible type="single">
          <AccordionItem value="color">
            <AccordionTrigger className="text-base font-medium">
              Color
            </AccordionTrigger>
            <AccordionContent>
              <div className="grid gap-2">
                <Label className="flex items-center gap-2 font-normal">
                  <Checkbox id="color-black" />
                  Black{"\n                                    "}
                </Label>
                <Label className="flex items-center gap-2 font-normal">
                  <Checkbox id="color-red" />
                  Red{"\n                                    "}
                </Label>
                <Label className="flex items-center gap-2 font-normal">
                  <Checkbox id="color-blue" />
                  Blue{"\n                                    "}
                </Label>
                <Label className="flex items-center gap-2 font-normal">
                  <Checkbox id="color-green" />
                  Green{"\n                                    "}
                </Label>
                <Label className="flex items-center gap-2 font-normal">
                  <Checkbox id="color-white" />
                  White{"\n                                    "}
                </Label>
              </div>
            </AccordionContent>
          </AccordionItem>
        </Accordion>

        {/* accordion1 - price */}
        <Accordion collapsible type="single">
          <AccordionItem value="price">
            <AccordionTrigger className="text-base font-medium">
              Price
            </AccordionTrigger>
            <AccordionContent>
              <div className="grid gap-4">
                <div className="grid grid-cols-2 items-center gap-4">
                  <Label className="text-sm" htmlFor="price-min">
                    Min
                  </Label>
                  <Input
                    className="h-8"
                    id="price-min"
                    placeholder="0"
                    type="number"
                  />
                </div>
                <div className="grid grid-cols-2 items-center gap-4">
                  <Label className="text-sm" htmlFor="price-max">
                    Max
                  </Label>
                  <Input
                    className="h-8"
                    id="price-max"
                    placeholder="999"
                    type="number"
                  />
                </div>
              </div>
            </AccordionContent>
          </AccordionItem>
        </Accordion>
      </div>
  );
};

export default ProductFilters;
