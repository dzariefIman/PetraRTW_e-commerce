        </div>
    </main>

    <div id="logoutModal" class="modal">
        <div class="modal-box-sm">
            <h3>Confirm logout</h3>
            <p>Are you sure you want to logout?</p>
            <div class="logout-actions">
                <button id="logoutNo" class="btn btn-cancel">No, keep login</button>
                <button id="logoutYes" class="btn btn-danger">Yes, logout</button>
            </div>
        </div>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            var logoutLinks = document.querySelectorAll('a.pill.logout');
            var logoutModal = document.getElementById('logoutModal');
            var logoutYes = document.getElementById('logoutYes');
            var logoutNo = document.getElementById('logoutNo');
            var targetHref = null;

            logoutLinks.forEach(function(a) {
                a.addEventListener('click', function(e) {
                    e.preventDefault();
                    targetHref = this.getAttribute('href');
                    logoutModal.classList.add('open');
                });
            });

            logoutNo.addEventListener('click', function() {
                logoutModal.classList.remove('open');
            });

            logoutYes.addEventListener('click', function() {
                if (targetHref) location.href = targetHref;
            });

            logoutModal.addEventListener('click', function(e) {
                if (e.target === logoutModal) logoutModal.classList.remove('open');
            });
        });
    </script>
</body>
</html>
