import Footer from "@/components/organism/footer";
import { RegisterForm } from "../../components/organism/registerForm";
import HeaderMainFooterLayout from "@/components/template/headerMainFooterLayout";

export default function RegisterPage() {
  return (
    <>
      <HeaderMainFooterLayout
        main={<RegisterForm />}
        footer={<Footer />}
      />
    </>
  );
}
