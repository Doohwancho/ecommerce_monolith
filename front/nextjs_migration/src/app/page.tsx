import Footer from "@/components/organism/footer";
import HomeLayout from "@/components/template/homeLayout";
import Image from 'next/image';
import mainImage from '/public/assets/image/main-image.webp'
import HomeBanner from "@/components/organism/homeBanner";
import TopTenRatedProducts from "@/components/organism/topTenProducts";

export default function Home() {
  return (
    <>
      <HomeLayout>
        <Image src={mainImage} alt='main image' />
        <HomeBanner />
        <TopTenRatedProducts/>
      </HomeLayout>
    </>
  );
}
