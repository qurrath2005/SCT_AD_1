import http.server
import socketserver
import webbrowser
import threading
import time

PORT = 8080  # Changed port to avoid conflicts

Handler = http.server.SimpleHTTPRequestHandler

def open_browser():
    """Open browser after a short delay"""
    time.sleep(1)
    webbrowser.open(f'http://localhost:{PORT}')

# Start the browser opening thread
threading.Thread(target=open_browser).start()

# Start the server
with socketserver.TCPServer(("", PORT), Handler) as httpd:
    print(f"Server running at http://localhost:{PORT}")
    print("Press Ctrl+C to stop the server")
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        print("\nServer stopped.")
