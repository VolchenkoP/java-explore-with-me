INSERT INTO apps (app_name)
SELECT 'ewm-main-service'
WHERE (SELECT COUNT(app_name) FROM apps) < 1;