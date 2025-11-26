package com.easy.stazy.pgmanagement.owner.config;

import com.easy.stazy.pgmanagement.pg.dto.response.HelpItemDto;
import java.util.List;

public class HelpItemsConfig {
    public static final List<HelpItemDto> HELP_ITEMS = List.of(
        new HelpItemDto("How to add a new PG?", "To add a new PG, click on the Add New PG button on the PG Listings screen. Fill in all the required details including PG name, address, amenities, and photos. Once submitted, your PG will be added to your listings."),
        new HelpItemDto("How to mark the rent collection?", "It's very easy to mark the rent collection. From the home screen, you can view the list of tenants and their rent status. Click on the tenant whose rent you want to mark as collected, and update the payment status."),
        new HelpItemDto("How to approve the PG Booking?", "To approve the PG booking, please click on the quick action from home screen or you can go to manage bookings screen and approve or reject the request."),
        new HelpItemDto("How to manage ratings & reviews?", "To manage ratings and reviews, please click on the button in the manage section. You can view all reviews, respond to them, and share your review link with tenants."),
        new HelpItemDto("How to manage the complaints?", "To manage the complaints, you can find the button on the manage screen. Click on it to view all complaints, change their status, and communicate with tenants."),
        new HelpItemDto("How to hide the sales amount?", "To hide the sales amount on the home screen, you can click on the eye icon next to the sales figure. This will hide/show the amount as needed."),
        new HelpItemDto("How to get reports?", "To get reports, you can go to the reports section from the dashboard. You can generate various reports including rent collection, occupancy, and financial reports. Select the report type and month, then click Download."),
        new HelpItemDto("How to add/change UPI address for rent collection?", "To add or change your UPI address, go to Settings and find the UPI address field under Other details. Enter or update your UPI ID and click Verify to confirm it."),
        new HelpItemDto("How to add a new bed in a room?", "Go to Tenants & Rent section, find the room where you want to add a bed, and tap on 'Add New Bed' button. A new vacant bed will be added to that room."),
        new HelpItemDto("How to assign a tenant to a vacant bed?", "In the Tenants & Rent section, find the vacant bed and tap on the '+' icon. This will open the 'Add Tenant' form where you can fill in tenant details, upload ID proof, and create their profile."),
        new HelpItemDto("How to remove a bed from a room?", "Tap on the hamburger menu icon (three dots) next to the bed entry, then select 'Remove Bed' from the bottom sheet. Confirm the action to remove the bed."),
        new HelpItemDto("How to edit tenant details?", "Go to Tenants & Rent section, tap on the hamburger menu icon next to the tenant's name, and select edit option. You can update tenant information, ID proofs, and photos."),
        new HelpItemDto("How to set tenant exit date?", "Open the tenant details by tapping the hamburger menu icon, then select 'Set Exit Date' option. Choose the date when the tenant will be vacating."),
        new HelpItemDto("How to upload tenant ID proof and photos?", "When adding or editing a tenant, scroll to the ID Proof section and tap 'Upload ID Proof'. You can choose multiple images from gallery or camera. Similarly, tap 'Upload Tenant Photo' to add their photo."),
        new HelpItemDto("What payment methods are supported?", "Stayzy supports multiple payment methods including UPI, bank transfer, and online payments through Razorpay. Tenants can pay rent directly through the app."),
        new HelpItemDto("How to track occupancy rates?", "On the dashboard, select 'Occupancy' from the metric dropdown. You can view occupancy rates for different time periods and compare across your PG properties."),
        new HelpItemDto("How to update PG amenities and rules?", "Go to PG Listings, select your PG, and tap edit. You can update amenities, house rules, photos, pricing, and other details. Make sure to save changes."),
        new HelpItemDto("How do tenants search for PG?", "Tenants can search for PG accommodations in the tenant app by location, price range, amenities, and sharing type. They can view photos, reviews, and amenities before booking."),
        new HelpItemDto("How do tenants book a PG?", "In the tenant app, after selecting a PG, tenants can tap 'Book Now' button, select their rental type (daily/monthly), choose check-in date, and submit the booking request. Owners will receive the request for approval."),
        new HelpItemDto("How do tenants pay rent?", "Tenants can pay rent through the tenant app using UPI, cards, or net banking. They will receive payment reminders, and owners get instant notifications when rent is paid."),
        new HelpItemDto("How can tenants raise complaints?", "In the tenant app, tenants can go to the complaints section, describe their issue, attach photos if needed, and submit. Owners receive instant notifications and can update the complaint status."),
        new HelpItemDto("Can tenants leave reviews?", "Yes, tenants can leave ratings and reviews for PGs they have stayed in. Reviews help other tenants make informed decisions and help owners improve their services."),
        new HelpItemDto("How to contact tenants?", "You can contact tenants directly through phone, chat, or WhatsApp. Contact icons are available in the tenant details, complaint cards, and booking requests."),
        new HelpItemDto("What is Stayzy Wallet?", "Stayzy Wallet is a digital wallet where your rent collections and other payments are stored. You can add money to your wallet and use it for various transactions within the app."),
        new HelpItemDto("How to download tenant data?", "Go to the Reports section, select the type of data you want to download (tenant list, rent collection, etc.), choose the time period, and tap Download. The file will be saved to your device."),
        new HelpItemDto("How to verify email and GSTIN?", "Go to Settings, find the email or GSTIN field, enter the details, and tap 'Verify'. You will receive a verification code via email or SMS. Enter the code to complete verification."),
        new HelpItemDto("How to share bank account details with tenants?", "In Settings, under Bank account details section, tap on 'Share Details'. This will create a shareable message with your bank details that you can send to tenants."),
        new HelpItemDto("What happens after I select a subscription plan?", "After selecting a plan, you will be directed to the payment screen. Complete the payment, and your plan will be activated immediately. You can then start listing your PGs."),
        new HelpItemDto("How to filter complaints by status?", "In the Complaints screen, use the filter dropdown at the top to select status like 'Pending action', 'Work in progress', 'Resolved', etc. The list will show only complaints with that status."),
        new HelpItemDto("How to filter reviews by rating or PG?", "In the Ratings & Reviews screen, use the filter dropdowns to select a specific PG name or star rating. You can also share your review link with tenants.")
    );
}

