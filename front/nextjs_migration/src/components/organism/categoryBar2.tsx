"use client";

import Link from "next/link";

import { SiNike } from "react-icons/si";
import { cn } from "@/lib/utils";

import React, { useState, useEffect, useRef } from "react";

import {
  Sheet,
  SheetClose,
  SheetContent,
  SheetDescription,
  SheetFooter,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/molecule/sheet";
import { Button } from "../atom/button";
import { Label } from "../atom/label";
import { Input } from "../atom/input";

// const SHEET_SIDES = ["top", "right", "bottom", "left"] as const

// type SheetSide = (typeof SHEET_SIDES)[number]

const menCategory = {
  //structure
  //중 카테고리 : { 하 카테고리1, 하 카테고리2, 하 카테고리3 }
  "New & Featured": {
    "신제품 전체": "category_id",
    "베스트 셀러": "category_id",
  },
  Collections: {
    "드라이 핏": "category_id",
    축구화: "category_id",
  },
  신발: {
    "라이프 스타일": "category_id",
    축구: "category_id",
    러닝: "category_id",
    테니스: "category_id",
    골프: "category_id",
  },
  의류: {
    "민소매 & 탱크탑": "category_id",
    "반팔 티셔츠": "category_id",
    러닝: "category_id",
    "탑 & 티셔츠": "category_id",
    쇼츠: "category_id",
  },
  용품: {
    "모자 & 헤드밴드": "category_id",
    축구: "category_id",
    가방: "category_id",
    장갑: "category_id",
    농구: "category_id",
  },
};

const CategoryBar2: React.FC = () => {
  // const triggerRef = React.useRef<HTMLButtonElement>(null);

  //컨텐츠 내용 구상하기
  //중 카테고리 / 하 카테고리

  const [isOpen, setIsOpen] = useState(false);
  const timeoutRef = useRef<NodeJS.Timeout | null>(null);
  const containerRef = useRef<HTMLDivElement>(null);

  const handleMouseEnter = () => {
    if (timeoutRef.current) {
      clearTimeout(timeoutRef.current);
    }
    setIsOpen(true);
  };

  const handleMouseLeave = (e: React.MouseEvent) => {
    const relatedTarget = e.relatedTarget as HTMLElement;
    if (containerRef.current && !containerRef.current.contains(relatedTarget)) {
      timeoutRef.current = setTimeout(() => {
        setIsOpen(false);
      }, 1000);
    }
  };
  useEffect(() => {
    return () => {
      if (timeoutRef.current) {
        clearTimeout(timeoutRef.current);
      }
    };
  }, []);

  return (
    //ver2
    <div 
      ref={containerRef} 
      onMouseEnter={handleMouseEnter}
      onMouseLeave={handleMouseLeave}
    >
      <div className="flex justify-center">
        <Button variant="ghost">Men</Button>
        <Button variant="ghost">Women</Button>
        <Button variant="ghost">Kids</Button>
      </div>
      <Sheet open={isOpen} onOpenChange={setIsOpen}>
        <SheetContent side={"top"}>
          <div className="grid grid-cols-4 gap-2.5">
            {/* Your existing grid content */}
            <div>
              <SheetTitle>title</SheetTitle>
              <SheetDescription>description1</SheetDescription>
              <SheetDescription>description2</SheetDescription>
              <SheetDescription>description3</SheetDescription>
              <SheetDescription>description4</SheetDescription>
              <SheetDescription>description5</SheetDescription>
            </div>
            {/* Repeat for other columns */}
          </div>
        </SheetContent>
      </Sheet>
    </div>


    // ver3
    // <Sheet open={isOpen} onOpenChange={setIsOpen}>
    //   <div className="flex justify-center">
    //     <SheetTrigger asChild>
    //       <Button variant="ghost">{"Men"}</Button>
    //     </SheetTrigger>
    //     <SheetTrigger asChild>
    //       <Button variant="ghost">{"Women"}</Button>
    //     </SheetTrigger>
    //     <SheetTrigger asChild>
    //       <Button variant="ghost">{"Kids"}</Button>
    //     </SheetTrigger>
    //   </div>
    //   <SheetContent side={"top"}>
    //     <div className="grid grid-cols-4 gap-2.5">
    //       {/* <div className="col-span-4 p-2.5 border border-gray-300"> */}
    //       <div>
    //         <SheetTitle>title</SheetTitle>
    //         <SheetDescription>description1</SheetDescription>
    //         <SheetDescription>description2</SheetDescription>
    //         <SheetDescription>description3</SheetDescription>
    //         <SheetDescription>description4</SheetDescription>
    //         <SheetDescription>description5</SheetDescription>
    //       </div>
    //       <div>
    //         <SheetTitle>title</SheetTitle>
    //         <SheetDescription>description1</SheetDescription>
    //         <SheetDescription>description2</SheetDescription>
    //         <SheetDescription>description3</SheetDescription>
    //         <SheetDescription>description4</SheetDescription>
    //         <SheetDescription>description5</SheetDescription>
    //       </div>
    //       <div>
    //         <SheetTitle>title</SheetTitle>
    //         <SheetDescription>description1</SheetDescription>
    //         <SheetDescription>description2</SheetDescription>
    //         <SheetDescription>description3</SheetDescription>
    //         <SheetDescription>description4</SheetDescription>
    //         <SheetDescription>description5</SheetDescription>
    //       </div>
    //       <div>
    //         <SheetTitle>title</SheetTitle>
    //         <SheetDescription>description1</SheetDescription>
    //         <SheetDescription>description2</SheetDescription>
    //         <SheetDescription>description3</SheetDescription>
    //         <SheetDescription>description4</SheetDescription>
    //         <SheetDescription>description5</SheetDescription>
    //       </div>
    //     </div>
    //   </SheetContent>
    // </Sheet>
  );
};

export default CategoryBar2;
