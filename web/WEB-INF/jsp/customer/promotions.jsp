<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, petra.model.Promotion, petra.model.Feedback, java.text.SimpleDateFormat, java.time.LocalDate" %>
<%
    request.setAttribute("pageTitle", "Promotion");
    request.setAttribute("activePage", "promotions");
    List<Promotion> promotions = (List<Promotion>) request.getAttribute("promotions");
    if (promotions == null) promotions = new ArrayList<Promotion>();
    List<Feedback> feedbacks = (List<Feedback>) request.getAttribute("feedbacks");
    if (feedbacks == null) feedbacks = new ArrayList<Feedback>();
    String ctx = request.getContextPath();
    SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
    String today = LocalDate.now().toString();
%>
<jsp:include page="/WEB-INF/jsp/customer/_header.jsp" />

<% if (promotions.isEmpty()) { %>
    <div class="empty-state">
        <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="3" y="3" width="18" height="18" rx="2"/>
            <line x1="9" y1="9" x2="15" y2="15"/>
            <line x1="15" y1="9" x2="9" y2="15"/>
        </svg>
        <h3>No Promotions Available</h3>
        <p>Check back later for exciting deals and offers!</p>
    </div>
<% } else { %>
    <div class="promo-grid">
        <% for (Promotion promo : promotions) {
            String startFmt = "", endFmt = "";
            try { startFmt = sdf.format(new SimpleDateFormat("yyyy-MM-dd").parse(promo.getStartDate())); } catch (Exception e) { startFmt = promo.getStartDate(); }
            try { endFmt = sdf.format(new SimpleDateFormat("yyyy-MM-dd").parse(promo.getEndDate())); } catch (Exception e) { endFmt = promo.getEndDate(); }
            boolean isActive = promo.getStartDate() != null && promo.getEndDate() != null && promo.getStartDate().compareTo(today) <= 0 && promo.getEndDate().compareTo(today) >= 0;
            String link = "#";
            String onclick = "";
            if (promo.getProductId() != null) {
                link = "javascript:void(0)";
                onclick = "openProductPromo(" + promo.getProductId() + ")";
            } else if (promo.getCollection() != null && !promo.getCollection().isEmpty()) {
                link = ctx + "/home?group=" + java.net.URLEncoder.encode(promo.getCollection(), "UTF-8");
            }
        %>
            <a href="<%= link %>" class="promo-card"<%= onclick.isEmpty() ? "" : " onclick=\"" + onclick + "; return false;\"" %>>
                <% if (promo.getProductImage() != null && !promo.getProductImage().isEmpty()) { %>
                    <div class="promo-image">
                        <img src="<%= promo.getProductImage().startsWith("http") ? promo.getProductImage() : ctx + "/" + promo.getProductImage() %>" alt="<%= promo.getAdsTitle() %>">
                        <% if (isActive) { %>
                            <span class="promo-badge">Active Now</span>
                        <% } %>
                    </div>
                <% } else { %>
                    <div class="promo-image placeholder">
                        <%= promo.getAdsTitle() != null && !promo.getAdsTitle().isEmpty() ? promo.getAdsTitle().substring(0, 1).toUpperCase() : "P" %>
                        <% if (isActive) { %>
                            <span class="promo-badge">Active Now</span>
                        <% } %>
                    </div>
                <% } %>
                <div class="promo-content">
                    <div class="promo-title"><%= promo.getAdsTitle() %></div>
                    <% if (promo.getAdsDesc() != null && !promo.getAdsDesc().isEmpty()) { %>
                        <div class="promo-desc"><%= promo.getAdsDesc().replace("\n", "<br>") %></div>
                    <% } %>
                    <div class="promo-dates">
                        <div class="promo-date">
                            <span>Valid from</span>
                            <strong><%= startFmt %></strong>
                        </div>
                        <div class="promo-date">
                            <span>Until</span>
                            <strong><%= endFmt %></strong>
                        </div>
                    </div>
                </div>
            </a>
        <% } %>
    </div>
<% } %>

<% if (!feedbacks.isEmpty()) { %>
    <div class="feedback-section">
        <div class="feedback-header">
            <h2>Customer Feedback</h2>
        </div>
        <div class="feedback-grid">
            <% for (Feedback fb : feedbacks) {
                String fbDate = "";
                try { fbDate = sdf.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fb.getCreatedTime())); } catch (Exception e) { try { fbDate = sdf.format(new SimpleDateFormat("yyyy-MM-dd").parse(fb.getCreatedTime())); } catch (Exception e2) { fbDate = fb.getCreatedTime() != null && fb.getCreatedTime().length() >= 10 ? fb.getCreatedTime().substring(0, 10) : ""; } }
                String fbInitial = fb.getCustomerName() != null && !fb.getCustomerName().isEmpty() ? String.valueOf(Character.toUpperCase(fb.getCustomerName().charAt(0))) : "U";
                String fbText = fb.getFeedbackText();
                if (fbText != null && fbText.length() > 100) fbText = fbText.substring(0, 100) + "...";
            %>
                <div class="feedback-card" onclick="openFeedbackModalById(<%= fb.getFeedbackId() %>)">
                    <div class="feedback-title"><%= fb.getProductName() != null ? fb.getProductName() : "Product" %></div>
                    <div class="star-display">
                        <% for (int i = 1; i <= 5; i++) { %>
                            <span class="star<%= i <= fb.getRating() ? " filled" : "" %>">&#9733;</span>
                        <% } %>
                    </div>
                    <div class="feedback-text"><%= fbText != null ? fbText : "" %></div>
                    <div class="feedback-footer">
                        <div class="feedback-avatar"><%= fbInitial %></div>
                        <div class="feedback-info">
                            <div class="feedback-name"><%= fb.getCustomerName() != null ? fb.getCustomerName() : "Anonymous" %></div>
                            <div class="feedback-date"><%= fbDate %></div>
                        </div>
                    </div>
                </div>
            <% } %>
        </div>
    </div>
<% } %>

<div id="feedbackModal" class="modal">
    <div class="modal-box">
        <button class="modal-close" onclick="closeFeedbackModal()">&times;</button>
        <div class="modal-header">
            <h3 class="modal-title" id="fbModalTitle"></h3>
        </div>
        <div class="modal-rating" id="fbModalRating"></div>
        <p class="modal-text" id="fbModalText"></p>
        <div class="modal-image" id="fbModalImage"></div>
        <div class="modal-footer">
            <div class="modal-avatar" id="fbModalAvatar"></div>
            <div class="modal-footer-info">
                <div class="modal-name" id="fbModalName"></div>
                <p class="modal-date" id="fbModalDate"></p>
            </div>
        </div>
    </div>
</div>

<script>
    var ctx = '<%= ctx %>';

    var feedbackData = {};
    <% for (Feedback fb : feedbacks) { %>
        feedbackData[<%= fb.getFeedbackId() %>] = {
            productName: "<%= fb.getProductName() != null ? fb.getProductName().replace("\\", "\\\\").replace("\"", "&quot;") : "Product" %>",
            rating: <%= fb.getRating() %>,
            feedbackText: "<%= fb.getFeedbackText() != null ? fb.getFeedbackText().replace("\\", "\\\\").replace("\"", "&quot;").replace("\n", "\\n").replace("\r", "\\r") : "" %>",
            imagePath: "<%= fb.getProductImage() != null ? fb.getProductImage().replace("\\", "\\\\") : "" %>",
            customerName: "<%= fb.getCustomerName() != null ? fb.getCustomerName().replace("\\", "\\\\").replace("\"", "&quot;") : "Anonymous" %>",
            createdAt: "<%= fb.getCreatedTime() != null && fb.getCreatedTime().length() >= 10 ? fb.getCreatedTime().substring(0, 10) : "" %>"
        };
    <% } %>

    function openFeedbackModalById(id) {
        var fb = feedbackData[id];
        if (!fb) return;
        document.getElementById('fbModalTitle').textContent = fb.productName;
        var ratingHtml = '<div class="star-display">';
        for (var i = 1; i <= 5; i++) {
            ratingHtml += '<span class="star' + (i <= fb.rating ? ' filled' : '') + '">&#9733;</span>';
        }
        ratingHtml += '</div>';
        document.getElementById('fbModalRating').innerHTML = ratingHtml;
        document.getElementById('fbModalText').textContent = fb.feedbackText;
        var imgContainer = document.getElementById('fbModalImage');
        if (fb.imagePath && fb.imagePath !== '') {
            var src = fb.imagePath.startsWith('http') ? fb.imagePath : ctx + '/' + fb.imagePath;
            imgContainer.innerHTML = '<img src="' + src + '" alt="Feedback image">';
            imgContainer.style.display = 'block';
        } else {
            imgContainer.style.display = 'none';
        }
        var initial = fb.customerName.charAt(0).toUpperCase();
        document.getElementById('fbModalAvatar').textContent = initial;
        document.getElementById('fbModalName').textContent = fb.customerName;
        document.getElementById('fbModalDate').textContent = fb.createdAt;
        document.getElementById('feedbackModal').classList.add('open');
    }

    function closeFeedbackModal() {
        document.getElementById('feedbackModal').classList.remove('open');
    }

    document.getElementById('feedbackModal').addEventListener('click', function(e) {
        if (e.target === this) closeFeedbackModal();
    });

    function openProductPromo(productId) {
        window.location.href = ctx + '/product?id=' + productId;
    }
</script>

<jsp:include page="/WEB-INF/jsp/customer/_footer.jsp" />
