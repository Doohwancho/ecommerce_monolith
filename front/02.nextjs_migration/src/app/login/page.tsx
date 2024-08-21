import Footer from "@/components/organism/footer";
import { LoginForm } from "@/components/organism/loginForm";
import HeaderMainFooterLayout from "@/components/template/headerMainFooterLayout";

export const dynamic = "force-static";

export default function LoginPage() {
  return (
    <>
      <HeaderMainFooterLayout
        main={<LoginForm />}
        footer={<Footer />}
      />
    </>
  );
}
