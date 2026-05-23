import axios from 'axios';

export const getErrorMessage = (error: unknown) => {
    if (axios.isAxiosError(error)) {
        const message = error.response?.data?.message;
        if (typeof message === 'string' && message.trim()) {
            return message;
        }
    }

    if (error instanceof Error && error.message) {
        return error.message;
    }

    return 'Thao tác thất bại';
};
