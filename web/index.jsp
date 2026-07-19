<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Petra — Start</title>
    <style>
        body{margin:0;overflow:hidden;background:#f0efe9;font-family:system-ui,Segoe UI,Arial,sans-serif;height:100vh}
        .hero{height:100vh;display:flex;align-items:center;justify-content:center;padding:3.5vw;box-sizing:border-box}
        .card{background:#fff;padding:clamp(16px,2.5vw,28px);border-radius:14px;border:1px solid #eee;width:90%;max-width:1100px;display:grid;grid-template-columns:clamp(180px,35vw,420px) 1fr;gap:clamp(16px,2.5vw,28px);align-items:center;box-shadow:0 2px 6px rgba(0,0,0,0.04);max-height:85vh}
        .card img{width:100%;max-height:60vh;object-fit:contain;border-radius:14px;display:block}
        .card h1{font-size:clamp(18px,3vw,28px);margin:0 0 12px;color:#333}
        .card p{color:#555;margin-bottom:18px;line-height:1.6;font-size:clamp(13px,1.2vw,16px)}
        .card .btn{display:inline-block;background:#b8864b;color:#fff;padding:clamp(8px,1vw,10px) clamp(10px,1.2vw,14px);border-radius:8px;text-decoration:none;font-weight:600;transition:opacity .12s}
        .card .btn:hover{opacity:0.85}
        @media (max-width:700px){.card{grid-template-columns:1fr}.card img{max-width:250px;margin:0 auto}}
    </style>
</head>
<body>
    <main class="hero">
        <div class="card">
            <div>
                <img src="<%= request.getContextPath() %>/assets/petraIndexImage.jpg" alt="Petra">
            </div>
            <div>
                <h1>PETRA CUSTOMER SERVICE</h1>
                <p>Hi, our valued customers! Feel free to leave a review, share any complaints, or contact our staff if you experience any issues with our products.</p>
                <a class="btn" href="<%= request.getContextPath() %>/login">LOGIN</a>
            </div>
        </div>
    </main>
</body>
</html>
