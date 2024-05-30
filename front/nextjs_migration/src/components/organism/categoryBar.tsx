"use client";

import * as React from "react";
import Link from "next/link";

import { SiNike } from "react-icons/si";
import { cn } from "@/lib/utils";

import {
  NavigationMenu,
  NavigationMenuContent,
  NavigationMenuItem,
  NavigationMenuLink,
  NavigationMenuList,
  NavigationMenuTrigger,
  navigationMenuTriggerStyle,
} from "@/components/molecule/navigation-menu";

//data schema for category (nested category)
// {
//     "topCategoryId": 1,
//     "topCategoryName": "Men",
//     "midCategoryId": 7,
//     "midCategoryName": "Men's Shoes",
//     "lowCategoryId": 31,
//     "lowCategoryName": "CQolbJVGwz"
// },

const components: { title: string; href: string; description: string }[] = [
  {
    title: "A",
    href: "/docs/primitives/alert-dialog",
    description:
      "A modal dialog that interrupts the user with important content and expects a response.",
  },
  {
    title: "B",
    href: "/docs/primitives/hover-card",
    description:
      "For sighted users to preview content available behind a link.",
  },
  {
    title: "C",
    href: "/docs/primitives/progress",
    description:
      "Displays an indicator showing the completion progress of a task, typically displayed as a progress bar.",
  },
  {
    title: "D",
    href: "/docs/primitives/scroll-area",
    description: "Visually or semantically separates content.",
  },
  {
    title: "E",
    href: "/docs/primitives/tabs",
    description:
      "A set of layered sections of content—known as tab panels—that are displayed one at a time.",
  },
  // {
  //   title: "F",
  //   href: "/docs/primitives/tooltip",
  //   description:
  //     "A popup that displays information related to an element when the element receives keyboard focus or the mouse hovers over it.",
  // },
  // {
  //   title: "G",
  //   href: "/docs/primitives/tooltip",
  //   description:
  //     "A popup that displays information related to an element when the element receives keyboard focus or the mouse hovers over it.",
  // },
  // {
  //   title: "H",
  //   href: "/docs/primitives/tooltip",
  //   description:
  //     "A popup that displays information related to an element when the element receives keyboard focus or the mouse hovers over it.",
  // },
  // {
  //   title: "I",
  //   href: "/docs/primitives/tooltip",
  //   description:
  //     "A popup that displays information related to an element when the element receives keyboard focus or the mouse hovers over it.",
  // },
  // {
  //   title: "J",
  //   href: "/docs/primitives/tooltip",
  //   description:
  //     "A popup that displays information related to an element when the element receives keyboard focus or the mouse hovers over it.",
  // },
  // {
  //   title: "K",
  //   href: "/docs/primitives/tooltip",
  //   description:
  //     "A popup that displays information related to an element when the element receives keyboard focus or the mouse hovers over it.",
  // },
  // {
  //   title: "L",
  //   href: "/docs/primitives/tooltip",
  //   description:
  //     "A popup that displays information related to an element when the element receives keyboard focus or the mouse hovers over it.",
  // },
  // {
  //   title: "M",
  //   href: "/docs/primitives/tooltip",
  //   description:
  //     "A popup that displays information related to an element when the element receives keyboard focus or the mouse hovers over it.",
  // },
  // {
  //   title: "N",
  //   href: "/docs/primitives/tooltip",
  //   description:
  //     "A popup that displays information related to an element when the element receives keyboard focus or the mouse hovers over it.",
  // },
  // {
  //   title: "O",
  //   href: "/docs/primitives/tooltip",
  //   description:
  //     "A popup that displays information related to an element when the element receives keyboard focus or the mouse hovers over it.",
  // },
];

export function CategoryBar() {
  return (
    <div className="relative flex item-center justify-center h-[40px]">
      <SiNike className="absolute left-4 item-center h-8 w-8" />
      <NavigationMenu>
        <NavigationMenuList>
          {/* category 1 */}
          <NavigationMenuItem>
            <NavigationMenuTrigger>Men</NavigationMenuTrigger>
            <NavigationMenuContent>
              <div className="grid grid-flow-col justify-center gap-3 p-4">
                <ul className="lg:w-[100px]">
                  {/* <ul className="flex flex-col w-[400px] gap-3 p-4 md:w-[500px] md:grid-cols-3 lg:w-[600px]"> */}
                  <h2 className="font-bold block select-none space-y-1 rounded-md p-3 leading-none no-underline outline-none transition-colors hover:bg-accent hover:text-accent-foreground focus:bg-accent focus:text-accent-foreground">
                    신발
                  </h2>
                  {components.map((component) => (
                    <ListItem
                      key={component.title}
                      title={component.title}
                      href={component.href}
                    ></ListItem>
                  ))}
                </ul>

                <ul className="lg:w-[100px]">
                  {/* <ul className="flex flex-col w-[400px] gap-3 p-4 md:w-[500px] md:grid-cols-3 lg:w-[600px]"> */}
                  <h2 className="font-bold block select-none space-y-1 rounded-md p-3 leading-none no-underline outline-none transition-colors hover:bg-accent hover:text-accent-foreground focus:bg-accent focus:text-accent-foreground">
                    의류
                  </h2>
                  {components.map((component) => (
                    <ListItem
                      key={component.title}
                      title={component.title}
                      href={component.href}
                    ></ListItem>
                  ))}
                </ul>

                <ul className="lg:w-[100px]">
                  {/* <ul className="flex flex-col w-[400px] gap-3 p-4 md:w-[500px] md:grid-cols-3 lg:w-[600px]"> */}
                  <h2 className="font-bold block select-none space-y-1 rounded-md p-3 leading-none no-underline outline-none transition-colors hover:bg-accent hover:text-accent-foreground focus:bg-accent focus:text-accent-foreground">
                    용품
                  </h2>
                  {components.map((component) => (
                    <ListItem
                      key={component.title}
                      title={component.title}
                      href={component.href}
                    ></ListItem>
                  ))}
                </ul>
              </div>
            </NavigationMenuContent>
          </NavigationMenuItem>

          {/* category2 */}
          <NavigationMenuItem>
            <NavigationMenuTrigger>Women</NavigationMenuTrigger>
            <NavigationMenuContent>
              <div className="grid grid-flow-col justify-center gap-3 p-4">
                <ul className="lg:w-[100px]">
                  {/* <ul className="flex flex-col w-[400px] gap-3 p-4 md:w-[500px] md:grid-cols-3 lg:w-[600px]"> */}
                  <h2 className="font-bold block select-none space-y-1 rounded-md p-3 leading-none no-underline outline-none transition-colors hover:bg-accent hover:text-accent-foreground focus:bg-accent focus:text-accent-foreground">
                    신발
                  </h2>
                  {components.map((component) => (
                    <ListItem
                      key={component.title}
                      title={component.title}
                      href={component.href}
                    ></ListItem>
                  ))}
                </ul>

                <ul className="lg:w-[100px]">
                  {/* <ul className="flex flex-col w-[400px] gap-3 p-4 md:w-[500px] md:grid-cols-3 lg:w-[600px]"> */}
                  <h2 className="font-bold block select-none space-y-1 rounded-md p-3 leading-none no-underline outline-none transition-colors hover:bg-accent hover:text-accent-foreground focus:bg-accent focus:text-accent-foreground">
                    의류
                  </h2>
                  {components.map((component) => (
                    <ListItem
                      key={component.title}
                      title={component.title}
                      href={component.href}
                    ></ListItem>
                  ))}
                </ul>

                <ul className="lg:w-[100px]">
                  {/* <ul className="flex flex-col w-[400px] gap-3 p-4 md:w-[500px] md:grid-cols-3 lg:w-[600px]"> */}
                  <h2 className="font-bold block select-none space-y-1 rounded-md p-3 leading-none no-underline outline-none transition-colors hover:bg-accent hover:text-accent-foreground focus:bg-accent focus:text-accent-foreground">
                    용품
                  </h2>
                  {components.map((component) => (
                    <ListItem
                      key={component.title}
                      title={component.title}
                      href={component.href}
                    ></ListItem>
                  ))}
                </ul>
              </div>
            </NavigationMenuContent>
          </NavigationMenuItem>

          {/* category 3 */}
          <NavigationMenuItem>
            <NavigationMenuTrigger>Kids</NavigationMenuTrigger>
            <NavigationMenuContent>
              <div className="grid grid-flow-col justify-center gap-3 p-4">
                <ul className="lg:w-[100px]">
                  {/* <ul className="flex flex-col w-[400px] gap-3 p-4 md:w-[500px] md:grid-cols-3 lg:w-[600px]"> */}
                  <h2 className="font-bold block select-none space-y-1 rounded-md p-3 leading-none no-underline outline-none transition-colors hover:bg-accent hover:text-accent-foreground focus:bg-accent focus:text-accent-foreground">
                    신발
                  </h2>
                  {components.map((component) => (
                    <ListItem
                      key={component.title}
                      title={component.title}
                      href={component.href}
                    ></ListItem>
                  ))}
                </ul>

                <ul className="lg:w-[100px]">
                  {/* <ul className="flex flex-col w-[400px] gap-3 p-4 md:w-[500px] md:grid-cols-3 lg:w-[600px]"> */}
                  <h2 className="font-bold block select-none space-y-1 rounded-md p-3 leading-none no-underline outline-none transition-colors hover:bg-accent hover:text-accent-foreground focus:bg-accent focus:text-accent-foreground">
                    의류
                  </h2>
                  {components.map((component) => (
                    <ListItem
                      key={component.title}
                      title={component.title}
                      href={component.href}
                    ></ListItem>
                  ))}
                </ul>

                <ul className="lg:w-[100px]">
                  {/* <ul className="flex flex-col w-[400px] gap-3 p-4 md:w-[500px] md:grid-cols-3 lg:w-[600px]"> */}
                  <h2 className="font-bold block select-none space-y-1 rounded-md p-3 leading-none no-underline outline-none transition-colors hover:bg-accent hover:text-accent-foreground focus:bg-accent focus:text-accent-foreground">
                    용품
                  </h2>
                  {components.map((component) => (
                    <ListItem
                      key={component.title}
                      title={component.title}
                      href={component.href}
                    ></ListItem>
                  ))}
                </ul>
              </div>
            </NavigationMenuContent>
          </NavigationMenuItem>
        </NavigationMenuList>
      </NavigationMenu>
    </div>
  );
}

const ListItem = React.forwardRef<
  React.ElementRef<"a">,
  React.ComponentPropsWithoutRef<"a">
>(({ className, title, children, ...props }, ref) => {
  return (
    <li>
      <NavigationMenuLink asChild>
        <a
          ref={ref}
          className={cn(
            "block select-none space-y-1 rounded-md p-3 leading-none no-underline outline-none transition-colors hover:bg-accent hover:text-accent-foreground focus:bg-accent focus:text-accent-foreground",
            className
          )}
          {...props}
        >
          <div className="text-sm font-medium leading-none text-gray-400">
            {title}
          </div>
          <p className="line-clamp-2 text-sm leading-snug text-muted-foreground">
            {children}
          </p>
        </a>
      </NavigationMenuLink>
    </li>
  );
});
ListItem.displayName = "ListItem";
