-- Tự động tạo database cho từng service khi container Postgres khởi tạo lần đầu.
-- Script này chỉ chạy 1 lần duy nhất khi volume dữ liệu còn trống.

CREATE DATABASE restaurant_db;
CREATE DATABASE delivery_db;
