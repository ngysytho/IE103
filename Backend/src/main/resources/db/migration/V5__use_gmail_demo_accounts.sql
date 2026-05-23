UPDATE TAIKHOAN
SET EMAIL = 'quanly@gmail.com'
WHERE EMAIL = 'quanly@ie103.local'
  AND NOT EXISTS (SELECT 1 FROM TAIKHOAN WHERE EMAIL = 'quanly@gmail.com');

UPDATE TAIKHOAN
SET EMAIL = 'nhapkho@gmail.com'
WHERE EMAIL = 'nhapkho@ie103.local'
  AND NOT EXISTS (SELECT 1 FROM TAIKHOAN WHERE EMAIL = 'nhapkho@gmail.com');

UPDATE TAIKHOAN
SET EMAIL = 'xuatkho@gmail.com'
WHERE EMAIL = 'xuatkho@ie103.local'
  AND NOT EXISTS (SELECT 1 FROM TAIKHOAN WHERE EMAIL = 'xuatkho@gmail.com');
