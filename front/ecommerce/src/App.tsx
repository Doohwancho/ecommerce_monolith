import { Routes, Route } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from 'react-query';
import { ReactQueryDevtools } from 'react-query/devtools';
import './App.css'

import Register from "./pages/Register";
import Login from './pages/Login';
import Home from './pages/Home';
import Category from './pages/Category';
import Product from './pages/Product';

const queryClient = new QueryClient();

const App = () => {
  return (
      <QueryClientProvider client={queryClient}>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/register" element={<Register />} />
            <Route path="/login" element={<Login />} />
            <Route path="/category/:categoryName" element={<Category />} />
            <Route path="/product" element={<Product />} />
          </Routes>
        <ReactQueryDevtools initialIsOpen={false} />
      </QueryClientProvider>
  );
};

export default App;
