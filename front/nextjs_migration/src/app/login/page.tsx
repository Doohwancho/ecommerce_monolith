import Footer from "@/components/organism/footer";
import { LoginForm } from "@/components/organism/loginForm";
import { Navbar } from "@/components/organism/categoryBar";
import HeaderMainFooterLayout from "@/components/template/headerMainFooterLayout";

export default function LoginPage() {
  return (
    <>
      <HeaderMainFooterLayout
        navbar={<Navbar />}
        main={<LoginForm />}
        footer={<Footer />}
      />
    </>
  );
}
