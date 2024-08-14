import React from "react";
import TopNavBar from "../organism/topNavBar";
import Footer from "../organism/footer";
import CategoryBar from "../organism/categoryBar";

interface LayoutProps {
  children: React.ReactNode;
}

const HomeLayout: React.FC<LayoutProps> = ({ children }) => {
  return (
    <>
      {/* TODO: 모니터, device마다 화면 사이즈가 다를텐데, 어떻게 max-width of screen 정하지? */}
      <div className="max-w-screen-lg mx-auto">
        <TopNavBar />
        <CategoryBar />
        {children}
        <Footer />
      </div>
    </>
  );
};

export default HomeLayout;
