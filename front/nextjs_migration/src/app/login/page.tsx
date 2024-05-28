import Footer from "@/components/organism/footer";
import { LoginForm } from "@/components/organism/loginForm";
import { Navbar } from "@/components/organism/navbar";
import BasicLayout from "@/components/template/basicLayout";

export default function LoginPage() {
  return (
    <>
      <BasicLayout 
        navbar={<Navbar />}
        main={<LoginForm/>}
        footer={<Footer />}
      />
    </>
  );
}
