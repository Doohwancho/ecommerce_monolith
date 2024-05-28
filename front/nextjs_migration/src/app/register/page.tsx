import Footer from "@/components/organism/footer";
import { Navbar } from "@/components/organism/navbar";
import BasicLayout from "@/components/template/basicLayout";
import { RegisterForm } from "../../components/organism/registerForm";

export default function RegisterPage() {
  return (
    <>
      <BasicLayout 
        navbar={<Navbar />}
        main={<RegisterForm />}
        footer={<Footer />}
      />
    </>
  );
}
