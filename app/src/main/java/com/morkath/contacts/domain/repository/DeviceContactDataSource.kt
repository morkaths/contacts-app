package com.morkath.contacts.domain.repository

import com.morkath.contacts.domain.model.Contact

interface DeviceContactDataSource {
    /**
     * Lấy danh sách tất cả các liên hệ từ thiết bị.
     */
    suspend fun getDeviceContacts(): List<Contact>

    /**
     * Thêm một liên hệ mới vào danh bạ của thiết bị.
     * @param contact Đối tượng Contact chứa thông tin để thêm mới.
     * @return Trả về true nếu thêm thành công, ngược lại trả về false.
     */
    suspend fun addContactToDevice(contact: Contact): Long?

    /**
     * Cập nhật thông tin một liên hệ đã có trên thiết bị.
     * @param contact Đối tượng Contact chứa thông tin cần cập nhật.
     * @return Trả về true nếu cập nhật thành công, ngược lại trả về false.
     */
    suspend fun updateContactOnDevice(contact: Contact): Boolean

    /**
     * Xóa một liên hệ khỏi danh bạ của thiết bị.
     * @param contactId ID của liên hệ cần xóa.
     * @return Trả về true nếu xóa thành công, ngược lại trả về false.
     */
    suspend fun deleteContactFromDevice(contactId: Long): Boolean

}