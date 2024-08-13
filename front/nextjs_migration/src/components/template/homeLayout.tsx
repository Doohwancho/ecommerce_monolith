import React from "react";
import TopNavBar from "../organism/topNavBar";
import { CategoryBar } from "../organism/categoryBar";
import Footer from "../organism/footer";
import CategoryBar2 from "../organism/categoryBar2";
import CategoryBar3 from "../organism/categoryBar3";

interface LayoutProps {
  children: React.ReactNode;
}

const HomeLayout: React.FC<LayoutProps> = ({ children }) => {
  return (
    <>
      {/* TODO: 모니터, device마다 화면 사이즈가 다를텐데, 어떻게 max-width of screen 정하지? */}
      <div className="max-w-screen-lg mx-auto">
        <TopNavBar />
        {/* <CategoryBar /> */}
        {/* <CategoryBar2 /> */}
        <CategoryBar3 />
        {children}
        <Footer />
      </div>
    </>
  );
};

export default HomeLayout;
