let sequence = 0;

export const nextDocumentCode = (prefix: 'PN' | 'PX' | 'KK') => {
    sequence = (sequence + 1) % 1000;
    const value = ((Date.now() + sequence) % 2176782336)
        .toString(36)
        .toUpperCase()
        .padStart(6, '0');
    return `${prefix}${value}`.slice(0, 8);
};
