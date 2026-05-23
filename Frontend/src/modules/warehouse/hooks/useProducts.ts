import { useEffect, useState } from 'react';
import { Product } from '../types';
import { productService } from '../services/product.service';

export const useProducts = () => {
    const [data, setData] = useState<Product[]>([]);
    const [loading, setLoading] = useState(false);

    const fetchData = async () => {
        try {
            setLoading(true);
            const res = await productService.getAll();
            setData(res);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        void fetchData();
    }, []);

    return {
        data,
        loading,
        refetch: fetchData,
    };
};