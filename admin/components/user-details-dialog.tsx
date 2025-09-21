"use client"

import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Mail, Phone, Calendar, MapPin, Home, FileText, DollarSign, Clock, Lock, Unlock } from "lucide-react"

interface UserDetailsDialogProps {
  user: any
  userType: "landlord" | "tenant"
  open: boolean
  onOpenChange: (open: boolean) => void
}

export function UserDetailsDialog({ user, userType, open, onOpenChange }: UserDetailsDialogProps) {
  if (!user) return null

  const handleStatusToggle = () => {
    // TODO: Implement status toggle logic
    console.log(`Toggle status for ${userType} ${user.id}: ${user.status}`)
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-2xl max-h-[80vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Chi tiết {userType === "landlord" ? "người cho thuê" : "người thuê"}</DialogTitle>
          <DialogDescription>Thông tin chi tiết và lịch sử hoạt động của {user.name}</DialogDescription>
        </DialogHeader>

        <div className="space-y-6">
          {/* User Profile */}
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Thông tin cá nhân</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="flex items-start space-x-4">
                <Avatar className="h-16 w-16">
                  <AvatarImage src={user.avatar || "/placeholder.svg"} alt={user.name} />
                  <AvatarFallback className="text-lg">{user.name.charAt(0)}</AvatarFallback>
                </Avatar>
                <div className="flex-1 space-y-2">
                  <div className="flex items-center justify-between">
                    <h3 className="text-xl font-semibold">{user.name}</h3>
                    <Badge variant={user.status === "active" ? "default" : "destructive"}>
                      {user.status === "active" ? "Hoạt động" : "Bị khóa"}
                    </Badge>
                  </div>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
                    <div className="flex items-center space-x-2">
                      <Mail className="h-4 w-4 text-muted-foreground" />
                      <span>{user.email}</span>
                    </div>
                    <div className="flex items-center space-x-2">
                      <Phone className="h-4 w-4 text-muted-foreground" />
                      <span>{user.phone}</span>
                    </div>
                    <div className="flex items-center space-x-2">
                      <Calendar className="h-4 w-4 text-muted-foreground" />
                      <span>Tham gia: {new Date(user.joinDate).toLocaleDateString("vi-VN")}</span>
                    </div>
                    <div className="flex items-center space-x-2">
                      <Clock className="h-4 w-4 text-muted-foreground" />
                      <span>Đăng nhập cuối: {new Date(user.lastLogin).toLocaleDateString("vi-VN")}</span>
                    </div>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Statistics */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            {userType === "landlord" ? (
              <>
                <Card>
                  <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                    <CardTitle className="text-sm font-medium">Số phòng</CardTitle>
                    <Home className="h-4 w-4 text-muted-foreground" />
                  </CardHeader>
                  <CardContent>
                    <div className="text-2xl font-bold">{user.propertiesCount}</div>
                    <p className="text-xs text-muted-foreground">Phòng đã đăng</p>
                  </CardContent>
                </Card>
                <Card>
                  <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                    <CardTitle className="text-sm font-medium">Doanh thu</CardTitle>
                    <DollarSign className="h-4 w-4 text-muted-foreground" />
                  </CardHeader>
                  <CardContent>
                    <div className="text-2xl font-bold">{(user.totalRevenue / 1000000).toFixed(1)}M</div>
                    <p className="text-xs text-muted-foreground">VND tổng cộng</p>
                  </CardContent>
                </Card>
                <Card>
                  <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                    <CardTitle className="text-sm font-medium">Đánh giá</CardTitle>
                  </CardHeader>
                  <CardContent>
                    <div className="text-2xl font-bold">4.8</div>
                    <p className="text-xs text-muted-foreground">Điểm trung bình</p>
                  </CardContent>
                </Card>
              </>
            ) : (
              <>
                <Card>
                  <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                    <CardTitle className="text-sm font-medium">Lịch sử thuê</CardTitle>
                    <FileText className="h-4 w-4 text-muted-foreground" />
                  </CardHeader>
                  <CardContent>
                    <div className="text-2xl font-bold">{user.rentHistory}</div>
                    <p className="text-xs text-muted-foreground">Lần thuê phòng</p>
                  </CardContent>
                </Card>
                <Card>
                  <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                    <CardTitle className="text-sm font-medium">Trạng thái</CardTitle>
                  </CardHeader>
                  <CardContent>
                    <div className="text-lg font-bold">{user.currentContract ? "Đang thuê" : "Trống"}</div>
                    <p className="text-xs text-muted-foreground">Hợp đồng hiện tại</p>
                  </CardContent>
                </Card>
                <Card>
                  <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                    <CardTitle className="text-sm font-medium">Đánh giá</CardTitle>
                  </CardHeader>
                  <CardContent>
                    <div className="text-2xl font-bold">4.5</div>
                    <p className="text-xs text-muted-foreground">Điểm từ chủ trọ</p>
                  </CardContent>
                </Card>
              </>
            )}
          </div>

          {/* Current Contract/Properties */}
          {userType === "tenant" && user.currentContract && (
            <Card>
              <CardHeader>
                <CardTitle className="text-lg">Hợp đồng hiện tại</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-2">
                  <div className="flex items-center space-x-2">
                    <MapPin className="h-4 w-4 text-muted-foreground" />
                    <span>{user.currentContract}</span>
                  </div>
                  <div className="text-sm text-muted-foreground">Bắt đầu: 01/01/2024 - Kết thúc: 31/12/2024</div>
                  <div className="text-sm">
                    Giá thuê: <span className="font-medium">5.000.000 VND/tháng</span>
                  </div>
                </div>
              </CardContent>
            </Card>
          )}

          {/* Actions */}
          <div className="flex justify-end space-x-2 pt-4 border-t">
            <Button variant="outline" onClick={() => onOpenChange(false)}>
              Đóng
            </Button>
            <Button onClick={handleStatusToggle} variant={user.status === "active" ? "destructive" : "default"}>
              {user.status === "active" ? (
                <>
                  <Lock className="mr-2 h-4 w-4" />
                  Khóa tài khoản
                </>
              ) : (
                <>
                  <Unlock className="mr-2 h-4 w-4" />
                  Mở khóa tài khoản
                </>
              )}
            </Button>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  )
}
