import { RouterProvider } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import { router } from './app/routes';

const App = () => {
  return (
    <ConfigProvider
      theme={{
        token: {
          borderRadius: 10,
        },
      }}
    >
      <RouterProvider router={router} />
    </ConfigProvider>
  );
};

export default App;