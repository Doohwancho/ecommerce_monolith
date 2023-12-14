import { Routes, Route } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from 'react-query';
import { ReactQueryDevtools } from 'react-query/devtools';

//template
import Header from './components/common/Header'
import TopNav from './components/common/TopNav/TopNav';
import Footer from './components/common/Footer'

//pages
import Register from "./pages/Register";
import Login from './pages/Login';
import Home from './pages/Home';
import Category from './pages/Category';
import Product from './pages/Product';

const queryClient = new QueryClient();

const App = () => {
  const Layout = ({ children }) => (
    <>
      <Header />
      <TopNav />
      {children}
      <Footer />
    </>
  );

  return (
      <QueryClientProvider client={queryClient}>
          <Routes>
            <Route path="/register" element={<Register />} />
            <Route path="/login" element={<Login />} />
            <Route path="/" element={<Layout><Home /></Layout>} />
            <Route path="/products/category/:lowCategoryId" element={<Layout><Category /></Layout>} />
            <Route path="/products/:productId" element={<Layout><Product /></Layout>} />
          </Routes>
        <ReactQueryDevtools initialIsOpen={false} />
      </QueryClientProvider>
  );
};

export default App;
