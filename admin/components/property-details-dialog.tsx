"use client"

import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { MapPin, DollarSign, Calendar, User, Home, Wifi, Car, Utensils } from "lucide-react"

interface PropertyDetailsDialogProps {
  property: any
  open: boolean
  onOpenChange: (open: boolean) => void
}

export function PropertyDetailsDialog({ property, open, onOpenChange }: PropertyDetailsDialogProps) {
  if (!property) return null

  const getStatusBadge = (status: string) => {
    switch (status) {
      case "pending":
        return <Badge variant="secondary">Chờ duyệt</Badge>
      case "approved":
        return <Badge variant="default">Đã duyệt</Badge>
      case "rejected":
        return <Badge variant="destructive">Bị từ chối</Badge>
      default:
        return <Badge variant="outline">Không xác định</Badge>
    }
  }

  const getUtilityIcon = (utility: string) => {
    switch (utility.toLowerCase()) {
      case "wifi":
        return <Wifi className="h-4 w-4" />
      case "điều hòa":
        return <Home className="h-4 w-4" />
      case "bếp gas":
      case "bếp từ":
        return <Utensils className="h-4 w-4" />
      case "chỗ đậu xe":
        return <Car className="h-4 w-4" />
      default:
        return <Home className="h-4 w-4" />
    }
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-4xl max-h-[80vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Chi tiết bài đăng</DialogTitle>
          <DialogDescription>Thông tin chi tiết về phòng trọ</DialogDescription>
        </DialogHeader>

        <div className="space-y-6">
          {/* Property Images */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {property.images.map((image: string, index: number) => (
              <img
                key={index}
                src={image || "/placeholder.svg?height=300&width=400"}
                alt={`${property.title} - Hình ${index + 1}`}
                className="w-full h-48 object-cover rounded-lg"
              />
            ))}
          </div>

          {/* Property Info */}
          <Card>
            <CardHeader>
              <div className="flex items-start justify-between">
                <div>
                  <CardTitle className="text-xl">{property.title}</CardTitle>
                  <div className="flex items-center space-x-4 mt-2 text-sm text-muted-foreground">
                    <div className="flex items-center">
                      <Calendar className="h-4 w-4 mr-1" />
                      Đăng ngày: {new Date(property.createdDate).toLocaleDateString("vi-VN")}
                    </div>
                    <div className="flex items-center">
                      <User className="h-4 w-4 mr-1" />
                      {property.landlord}
                    </div>
                  </div>
                </div>
                {getStatusBadge(property.status)}
              </div>
            </CardHeader>
            <CardContent className="space-y-4">
              {/* Price and Area */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="flex items-center space-x-2">
                  <DollarSign className="h-5 w-5 text-green-600" />
                  <div>
                    <div className="font-semibold text-lg">{property.price.toLocaleString()} VND/tháng</div>
                    <div className="text-sm text-muted-foreground">Giá thuê</div>
                  </div>
                </div>
                <div className="flex items-center space-x-2">
                  <Home className="h-5 w-5 text-blue-600" />
                  <div>
                    <div className="font-semibold text-lg">{property.area} m²</div>
                    <div className="text-sm text-muted-foreground">Diện tích</div>
                  </div>
                </div>
              </div>

              {/* Address */}
              <div className="flex items-start space-x-2">
                <MapPin className="h-5 w-5 text-red-600 mt-0.5" />
                <div>
                  <div className="font-medium">{property.address}</div>
                  <div className="text-sm text-muted-foreground">Địa chỉ</div>
                </div>
              </div>

              {/* Description */}
              <div>
                <h4 className="font-medium mb-2">Mô tả</h4>
                <p className="text-sm text-muted-foreground">{property.description}</p>
              </div>

              {/* Utilities */}
              <div>
                <h4 className="font-medium mb-2">Tiện ích</h4>
                <div className="flex flex-wrap gap-2">
                  {property.utilities.map((utility: string, index: number) => (
                    <Badge key={index} variant="outline" className="flex items-center space-x-1">
                      {getUtilityIcon(utility)}
                      <span>{utility}</span>
                    </Badge>
                  ))}
                </div>
              </div>

              {/* Rejection Reason */}
              {property.status === "rejected" && property.rejectionReason && (
                <div className="p-4 bg-red-50 dark:bg-red-900/20 rounded-lg border border-red-200 dark:border-red-800">
                  <h4 className="font-medium text-red-800 dark:text-red-200 mb-2">Lý do từ chối</h4>
                  <p className="text-sm text-red-700 dark:text-red-300">{property.rejectionReason}</p>
                </div>
              )}
            </CardContent>
          </Card>

          {/* Actions */}
          <div className="flex justify-end space-x-2 pt-4 border-t">
            <Button variant="outline" onClick={() => onOpenChange(false)}>
              Đóng
            </Button>
            {property.status === "pending" && (
              <>
                <Button variant="destructive">Từ chối</Button>
                <Button>Duyệt tin</Button>
              </>
            )}
          </div>
        </div>
      </DialogContent>
    </Dialog>
  )
}
