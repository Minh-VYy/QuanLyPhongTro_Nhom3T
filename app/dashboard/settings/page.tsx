"use client"

import { useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { Switch } from "@/components/ui/switch"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Badge } from "@/components/ui/badge"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Settings, Save, Upload, Shield, Bell, Mail, Globe, DollarSign, Users, Trash2, Plus, Edit } from "lucide-react"

export default function SettingsPage() {
  const [settings, setSettings] = useState({
    // General Settings
    siteName: "Quản lý phòng trọ",
    siteDescription: "Hệ thống quản lý phòng trọ chuyên nghiệp",
    contactEmail: "admin@phongtro.com",
    contactPhone: "0123456789",
    address: "123 Nguyễn Huệ, Quận 1, TP.HCM",

    // Notification Settings
    emailNotifications: true,
    smsNotifications: false,
    pushNotifications: true,
    reportNotifications: true,
    paymentNotifications: true,

    // Payment Settings
    commissionRate: 5,
    autoApproval: false,
    requireDeposit: true,
    maxListingDuration: 90,

    // Security Settings
    twoFactorAuth: false,
    sessionTimeout: 30,
    passwordExpiry: 90,
    maxLoginAttempts: 5,
  })

  const [admins] = useState([
    {
      id: 1,
      name: "Admin Chính",
      email: "admin@phongtro.com",
      role: "super_admin",
      status: "active",
      lastLogin: "2024-12-20T10:30:00",
      avatar: "/placeholder.svg?height=40&width=40",
    },
    {
      id: 2,
      name: "Moderator 1",
      email: "mod1@phongtro.com",
      role: "moderator",
      status: "active",
      lastLogin: "2024-12-19T15:20:00",
      avatar: "/placeholder.svg?height=40&width=40",
    },
    {
      id: 3,
      name: "Support Staff",
      email: "support@phongtro.com",
      role: "support",
      status: "inactive",
      lastLogin: "2024-12-15T09:10:00",
      avatar: "/placeholder.svg?height=40&width=40",
    },
  ])

  const handleSettingChange = (key: string, value: any) => {
    setSettings((prev) => ({ ...prev, [key]: value }))
  }

  const handleSaveSettings = () => {
    // TODO: Implement save settings logic
    console.log("Saving settings:", settings)
  }

  const getRoleBadge = (role: string) => {
    switch (role) {
      case "super_admin":
        return <Badge variant="default">Super Admin</Badge>
      case "moderator":
        return <Badge variant="secondary">Moderator</Badge>
      case "support":
        return <Badge variant="outline">Support</Badge>
      default:
        return <Badge variant="outline">Unknown</Badge>
    }
  }

  const getStatusBadge = (status: string) => {
    switch (status) {
      case "active":
        return <Badge variant="default">Hoạt động</Badge>
      case "inactive":
        return <Badge variant="secondary">Không hoạt động</Badge>
      default:
        return <Badge variant="outline">Không xác định</Badge>
    }
  }

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div>
        <h1 className="text-2xl font-bold text-foreground">Cài đặt hệ thống</h1>
        <p className="text-muted-foreground">Quản lý cấu hình và thiết lập hệ thống</p>
      </div>

      <Tabs defaultValue="general" className="space-y-4">
        <TabsList className="grid w-full grid-cols-5">
          <TabsTrigger value="general">Chung</TabsTrigger>
          <TabsTrigger value="notifications">Thông báo</TabsTrigger>
          <TabsTrigger value="payments">Thanh toán</TabsTrigger>
          <TabsTrigger value="security">Bảo mật</TabsTrigger>
          <TabsTrigger value="admins">Quản trị viên</TabsTrigger>
        </TabsList>

        {/* General Settings */}
        <TabsContent value="general">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center space-x-2">
                  <Globe className="h-5 w-5" />
                  <span>Thông tin chung</span>
                </CardTitle>
                <CardDescription>Cấu hình thông tin cơ bản của hệ thống</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="siteName">Tên hệ thống</Label>
                  <Input
                    id="siteName"
                    value={settings.siteName}
                    onChange={(e) => handleSettingChange("siteName", e.target.value)}
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="siteDescription">Mô tả</Label>
                  <Textarea
                    id="siteDescription"
                    value={settings.siteDescription}
                    onChange={(e) => handleSettingChange("siteDescription", e.target.value)}
                    rows={3}
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="contactEmail">Email liên hệ</Label>
                  <Input
                    id="contactEmail"
                    type="email"
                    value={settings.contactEmail}
                    onChange={(e) => handleSettingChange("contactEmail", e.target.value)}
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="contactPhone">Số điện thoại</Label>
                  <Input
                    id="contactPhone"
                    value={settings.contactPhone}
                    onChange={(e) => handleSettingChange("contactPhone", e.target.value)}
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="address">Địa chỉ</Label>
                  <Textarea
                    id="address"
                    value={settings.address}
                    onChange={(e) => handleSettingChange("address", e.target.value)}
                    rows={2}
                  />
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle className="flex items-center space-x-2">
                  <Upload className="h-5 w-5" />
                  <span>Logo và hình ảnh</span>
                </CardTitle>
                <CardDescription>Tải lên logo và hình ảnh đại diện</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="space-y-2">
                  <Label>Logo hệ thống</Label>
                  <div className="flex items-center space-x-4">
                    <div className="w-16 h-16 bg-muted rounded-lg flex items-center justify-center">
                      <Settings className="h-8 w-8 text-muted-foreground" />
                    </div>
                    <Button variant="outline">
                      <Upload className="mr-2 h-4 w-4" />
                      Tải lên logo
                    </Button>
                  </div>
                </div>
                <div className="space-y-2">
                  <Label>Favicon</Label>
                  <div className="flex items-center space-x-4">
                    <div className="w-8 h-8 bg-muted rounded flex items-center justify-center">
                      <Settings className="h-4 w-4 text-muted-foreground" />
                    </div>
                    <Button variant="outline" size="sm">
                      <Upload className="mr-2 h-3 w-3" />
                      Tải lên
                    </Button>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>
        </TabsContent>

        {/* Notification Settings */}
        <TabsContent value="notifications">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center space-x-2">
                <Bell className="h-5 w-5" />
                <span>Cài đặt thông báo</span>
              </CardTitle>
              <CardDescription>Quản lý các loại thông báo và cảnh báo</CardDescription>
            </CardHeader>
            <CardContent className="space-y-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="space-y-4">
                  <h4 className="font-medium">Kênh thông báo</h4>
                  <div className="space-y-3">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center space-x-2">
                        <Mail className="h-4 w-4" />
                        <Label htmlFor="emailNotifications">Email</Label>
                      </div>
                      <Switch
                        id="emailNotifications"
                        checked={settings.emailNotifications}
                        onCheckedChange={(checked) => handleSettingChange("emailNotifications", checked)}
                      />
                    </div>
                    <div className="flex items-center justify-between">
                      <div className="flex items-center space-x-2">
                        <Bell className="h-4 w-4" />
                        <Label htmlFor="pushNotifications">Push notification</Label>
                      </div>
                      <Switch
                        id="pushNotifications"
                        checked={settings.pushNotifications}
                        onCheckedChange={(checked) => handleSettingChange("pushNotifications", checked)}
                      />
                    </div>
                    <div className="flex items-center justify-between">
                      <div className="flex items-center space-x-2">
                        <span className="h-4 w-4 text-center text-xs">📱</span>
                        <Label htmlFor="smsNotifications">SMS</Label>
                      </div>
                      <Switch
                        id="smsNotifications"
                        checked={settings.smsNotifications}
                        onCheckedChange={(checked) => handleSettingChange("smsNotifications", checked)}
                      />
                    </div>
                  </div>
                </div>

                <div className="space-y-4">
                  <h4 className="font-medium">Loại thông báo</h4>
                  <div className="space-y-3">
                    <div className="flex items-center justify-between">
                      <Label htmlFor="reportNotifications">Báo cáo mới</Label>
                      <Switch
                        id="reportNotifications"
                        checked={settings.reportNotifications}
                        onCheckedChange={(checked) => handleSettingChange("reportNotifications", checked)}
                      />
                    </div>
                    <div className="flex items-center justify-between">
                      <Label htmlFor="paymentNotifications">Thanh toán</Label>
                      <Switch
                        id="paymentNotifications"
                        checked={settings.paymentNotifications}
                        onCheckedChange={(checked) => handleSettingChange("paymentNotifications", checked)}
                      />
                    </div>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        {/* Payment Settings */}
        <TabsContent value="payments">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center space-x-2">
                <DollarSign className="h-5 w-5" />
                <span>Cài đặt thanh toán</span>
              </CardTitle>
              <CardDescription>Cấu hình các thông số liên quan đến thanh toán</CardDescription>
            </CardHeader>
            <CardContent className="space-y-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="space-y-4">
                  <div className="space-y-2">
                    <Label htmlFor="commissionRate">Tỷ lệ hoa hồng (%)</Label>
                    <Input
                      id="commissionRate"
                      type="number"
                      value={settings.commissionRate}
                      onChange={(e) => handleSettingChange("commissionRate", Number.parseFloat(e.target.value))}
                      min="0"
                      max="100"
                      step="0.1"
                    />
                    <p className="text-xs text-muted-foreground">Phần trăm hoa hồng từ mỗi giao dịch</p>
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="maxListingDuration">Thời gian đăng tối đa (ngày)</Label>
                    <Input
                      id="maxListingDuration"
                      type="number"
                      value={settings.maxListingDuration}
                      onChange={(e) => handleSettingChange("maxListingDuration", Number.parseInt(e.target.value))}
                      min="1"
                      max="365"
                    />
                  </div>
                </div>

                <div className="space-y-4">
                  <h4 className="font-medium">Tùy chọn</h4>
                  <div className="space-y-3">
                    <div className="flex items-center justify-between">
                      <div>
                        <Label htmlFor="autoApproval">Tự động duyệt bài đăng</Label>
                        <p className="text-xs text-muted-foreground">Bài đăng sẽ được duyệt tự động</p>
                      </div>
                      <Switch
                        id="autoApproval"
                        checked={settings.autoApproval}
                        onCheckedChange={(checked) => handleSettingChange("autoApproval", checked)}
                      />
                    </div>
                    <div className="flex items-center justify-between">
                      <div>
                        <Label htmlFor="requireDeposit">Bắt buộc tiền cọc</Label>
                        <p className="text-xs text-muted-foreground">Yêu cầu tiền cọc cho mọi hợp đồng</p>
                      </div>
                      <Switch
                        id="requireDeposit"
                        checked={settings.requireDeposit}
                        onCheckedChange={(checked) => handleSettingChange("requireDeposit", checked)}
                      />
                    </div>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        {/* Security Settings */}
        <TabsContent value="security">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center space-x-2">
                <Shield className="h-5 w-5" />
                <span>Cài đặt bảo mật</span>
              </CardTitle>
              <CardDescription>Cấu hình các tùy chọn bảo mật hệ thống</CardDescription>
            </CardHeader>
            <CardContent className="space-y-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="space-y-4">
                  <div className="space-y-2">
                    <Label htmlFor="sessionTimeout">Thời gian hết phiên (phút)</Label>
                    <Input
                      id="sessionTimeout"
                      type="number"
                      value={settings.sessionTimeout}
                      onChange={(e) => handleSettingChange("sessionTimeout", Number.parseInt(e.target.value))}
                      min="5"
                      max="480"
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="passwordExpiry">Hết hạn mật khẩu (ngày)</Label>
                    <Input
                      id="passwordExpiry"
                      type="number"
                      value={settings.passwordExpiry}
                      onChange={(e) => handleSettingChange("passwordExpiry", Number.parseInt(e.target.value))}
                      min="30"
                      max="365"
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="maxLoginAttempts">Số lần đăng nhập tối đa</Label>
                    <Input
                      id="maxLoginAttempts"
                      type="number"
                      value={settings.maxLoginAttempts}
                      onChange={(e) => handleSettingChange("maxLoginAttempts", Number.parseInt(e.target.value))}
                      min="3"
                      max="10"
                    />
                  </div>
                </div>

                <div className="space-y-4">
                  <h4 className="font-medium">Tùy chọn bảo mật</h4>
                  <div className="space-y-3">
                    <div className="flex items-center justify-between">
                      <div>
                        <Label htmlFor="twoFactorAuth">Xác thực 2 bước</Label>
                        <p className="text-xs text-muted-foreground">Bắt buộc xác thực 2 bước cho admin</p>
                      </div>
                      <Switch
                        id="twoFactorAuth"
                        checked={settings.twoFactorAuth}
                        onCheckedChange={(checked) => handleSettingChange("twoFactorAuth", checked)}
                      />
                    </div>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        {/* Admin Management */}
        <TabsContent value="admins">
          <Card>
            <CardHeader>
              <div className="flex items-center justify-between">
                <div>
                  <CardTitle className="flex items-center space-x-2">
                    <Users className="h-5 w-5" />
                    <span>Quản lý quản trị viên</span>
                  </CardTitle>
                  <CardDescription>Quản lý tài khoản và phân quyền admin</CardDescription>
                </div>
                <Button>
                  <Plus className="mr-2 h-4 w-4" />
                  Thêm admin
                </Button>
              </div>
            </CardHeader>
            <CardContent>
              <div className="rounded-md border">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Quản trị viên</TableHead>
                      <TableHead>Email</TableHead>
                      <TableHead>Vai trò</TableHead>
                      <TableHead>Trạng thái</TableHead>
                      <TableHead>Đăng nhập cuối</TableHead>
                      <TableHead className="text-right">Thao tác</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {admins.map((admin) => (
                      <TableRow key={admin.id}>
                        <TableCell>
                          <div className="flex items-center space-x-3">
                            <Avatar className="h-8 w-8">
                              <AvatarImage src={admin.avatar || "/placeholder.svg"} alt={admin.name} />
                              <AvatarFallback>{admin.name.charAt(0)}</AvatarFallback>
                            </Avatar>
                            <div>
                              <div className="font-medium">{admin.name}</div>
                              <div className="text-sm text-muted-foreground">ID: {admin.id}</div>
                            </div>
                          </div>
                        </TableCell>
                        <TableCell>{admin.email}</TableCell>
                        <TableCell>{getRoleBadge(admin.role)}</TableCell>
                        <TableCell>{getStatusBadge(admin.status)}</TableCell>
                        <TableCell>
                          <div className="text-sm">{new Date(admin.lastLogin).toLocaleDateString("vi-VN")}</div>
                          <div className="text-xs text-muted-foreground">
                            {new Date(admin.lastLogin).toLocaleTimeString("vi-VN", {
                              hour: "2-digit",
                              minute: "2-digit",
                            })}
                          </div>
                        </TableCell>
                        <TableCell className="text-right">
                          <div className="flex items-center justify-end space-x-2">
                            <Button variant="ghost" size="sm">
                              <Edit className="h-4 w-4" />
                            </Button>
                            {admin.role !== "super_admin" && (
                              <Button variant="ghost" size="sm">
                                <Trash2 className="h-4 w-4" />
                              </Button>
                            )}
                          </div>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>

      {/* Save Button */}
      <div className="flex justify-end">
        <Button onClick={handleSaveSettings} className="min-w-32">
          <Save className="mr-2 h-4 w-4" />
          Lưu cài đặt
        </Button>
      </div>
    </div>
  )
}
