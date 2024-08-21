"use client"

import * as React from "react"
import { Slot } from "@radix-ui/react-slot"
import { cva, type VariantProps } from "class-variance-authority"

import { cn } from "@/lib/utils"

const buttonVariants = cva(
  "inline-flex items-center justify-center whitespace-nowrap rounded-md text-sm font-medium ring-offset-background transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50",
  {
    variants: {
      variant: {
        default: "bg-primary text-primary-foreground hover:bg-primary/90",
        destructive:
          "bg-destructive text-destructive-foreground hover:bg-destructive/90",
        outline:
          "border border-input bg-background hover:bg-accent hover:text-accent-foreground",
        secondary:
          "bg-secondary text-secondary-foreground hover:bg-secondary/80",
        ghost: "hover:bg-accent hover:text-accent-foreground",
        link: "text-primary underline-offset-4 hover:underline",
      },
      size: {
        default: "h-10 px-4 py-2",
        sm: "h-9 rounded-md px-3",
        lg: "h-11 rounded-md px-8",
        icon: "h-10 w-10",
      },
    },
    defaultVariants: {
      variant: "default",
      size: "default",
    },
  }
)

export interface ButtonProps
  extends React.ButtonHTMLAttributes<HTMLButtonElement>,
    VariantProps<typeof buttonVariants> {
  asChild?: boolean
}

const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(
  ({ className, variant, size, asChild = false, ...props }, ref) => {
    const Comp = asChild ? Slot : "button"

    //ghost variant인 경우, mouse hover이 mouse click의 역할을 한다. 
    const [isHovered, setIsHovered] = React.useState(false)

    const handleMouseEnter = (event: React.MouseEvent<HTMLButtonElement>) => {
      if(variant === "ghost") {
        setIsHovered(true)
        event.currentTarget.click()
      }
    }

    const handleMouseLeave = (event: React.MouseEvent<HTMLButtonElement>) => {
      if(variant === "ghost") {
        setIsHovered(false)
        event.currentTarget.click()
      }
    }
    // const [isClicked, setIsClicked] = React.useState(false)

    // const handleMouseEnter = (event: React.MouseEvent<HTMLButtonElement>) => {
    //   if (variant === "ghost") {
    //     setIsClicked(true)
    //     event.currentTarget.click()
    //   }
    // }

    // const handleMouseLeave = (event: React.MouseEvent<HTMLButtonElement>) => {
    //   if (variant === "ghost") {
    //     setIsClicked(false)
    //     event.currentTarget.click()
    //     // setTimeout(() => {
    //     //   const sheetContent = document.querySelector('.sheet-content');
    //     //   if (sheetContent) {
    //     //     sheetContent.setAttribute('data-state', 'closed');
    //     //   }
    //     // }, 300);
    //   }
    // }


    // const handleMouseEnter = (event: React.MouseEvent<HTMLButtonElement>) => {
    //   if (variant === "ghost") {
    //     event.currentTarget.click()
    //   }
    // }

    return (
      <Comp
        className={cn(buttonVariants({ variant, size, className }))}
        ref={ref}
        //hover시 onClick 되게끔 이벤트 추가
        onMouseEnter={handleMouseEnter}
        onMouseLeave={handleMouseLeave}
        data-state={isHovered ? "open" : "closed"}
        {...props}
      />
    )
  }
)
Button.displayName = "Button"

export { Button, buttonVariants }
