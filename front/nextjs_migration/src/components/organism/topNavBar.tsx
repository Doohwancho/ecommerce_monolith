const TopNavBar: React.FC = () => {
    return (
      <div className="bg-gray-200 flex justify-end items-center space-x-4 py-0.4 pr-4" >
        <a href="/register" className="text-black-500 hover:text-blue-700 text-sm">
          Register
        </a>
        <span className="text-gray-400">|</span>
        <a href="/login" className="text-black-500 hover:text-blue-700 text-sm">
          Login
        </a>
      </div>
    );
  };
  
  export default TopNavBar;