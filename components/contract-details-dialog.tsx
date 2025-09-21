"use client"

import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Avatar, AvatarFallback } from "@/components/ui/avatar"
import { Calendar, DollarSign, FileText, Home, User, CheckCircle, XCircle, Clock } from "lucide-react"

interface ContractDetailsDialogProps {
  contract: any
  open: boolean
  onOpenChange: (open: boolean) => void
}

export function ContractDetailsDialog({ contract, open, onOpenChange }: ContractDetailsDialogProps) {
  if (!contract) return null

  const getStatusBadge = (status: string) => {
    switch (status) {
      case "active":
        return (
          <Badge variant="default" className="flex items-center space-x-1">
            <CheckCircle className="h-3 w-3" />
            <span>Đang hiệu lực</span>
          </Badge>
        )
      case "expired":
        return (
          <Badge variant="secondary" className="flex items-center space-x-1">
            <Clock className="h-3 w-3" />
            <span>Hết hạn</span>
          </Badge>
        )
      case "terminated":
        return (
          <Badge variant="destructive" className="flex items-center space-x-1">
            <XCircle className="h-3 w-3" />
            <span>Đã chấm dứt</span>
          </Badge>
        )
      default:
        return <Badge variant="outline">Không xác định</Badge>
    }
  }

  const calculateDaysRemaining = () => {
    const endDate = new Date(contract.endDate)
    const today = new Date()
    const diffTime = endDate.getTime() - today.getTime()
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
    return diffDays
  }

  const daysRemaining = calculateDaysRemaining()

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-4xl max-h-[80vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Chi tiết hợp đồng</DialogTitle>
          <DialogDescription>Thông tin chi tiết về hợp đồng thuê phòng</DialogDescription>
        </DialogHeader>

        <div className="space-y-6">
          {/* Contract Header */}
          <Card>
            <CardHeader>
              <div className="flex items-start justify-between">
                <div>
                  <CardTitle className="text-xl flex items-center space-x-2">
                    <FileText className="h-6 w-6" />
                    <span>Hợp đồng {contract.id}</span>
                  </CardTitle>
                  <div className="flex items-center space-x-4 mt-2">
                    {getStatusBadge(contract.status)}
                    {contract.status === "active" && daysRemaining > 0 && (
                      <Badge variant="outline">{daysRemaining} ngày còn lại</Badge>
                    )}
                  </div>
                </div>
                <div className="text-sm text-muted-foreground">
                  <div className="flex items-center">
                    <Calendar className="h-4 w-4 mr-1" />
                    Ký ngày: {new Date(contract.signedDate).toLocaleDateString("vi-VN")}
                  </div>
                </div>
              </div>
            </CardHeader>
          </Card>

          {/* Parties Involved */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Card>
              <CardHeader>
                <CardTitle className="text-lg flex items-center">
                  <User className="h-5 w-5 mr-2" />
                  Người thuê
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="flex items-center space-x-3">
                  <Avatar className="h-12 w-12">
                    <AvatarFallback className="text-lg">{contract.tenant.charAt(0)}</AvatarFallback>
                  </Avatar>
                  <div>
                    <div className="font-medium text-lg">{contract.tenant}</div>
                    <div className="text-sm text-muted-foreground">ID: {contract.tenantId}</div>
                  </div>
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle className="text-lg flex items-center">
                  <Home className="h-5 w-5 mr-2" />
                  Chủ trọ
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="flex items-center space-x-3">
                  <Avatar className="h-12 w-12">
                    <AvatarFallback className="text-lg">{contract.landlord.charAt(0)}</AvatarFallback>
                  </Avatar>
                  <div>
                    <div className="font-medium text-lg">{contract.landlord}</div>
                    <div className="text-sm text-muted-foreground">ID: {contract.landlordId}</div>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Property and Financial Info */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Card>
              <CardHeader>
                <CardTitle className="text-lg">Thông tin phòng</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-2">
                  <div className="font-medium">{contract.property}</div>
                  <div className="text-sm text-muted-foreground">ID: {contract.propertyId}</div>
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle className="text-lg flex items-center">
                  <DollarSign className="h-5 w-5 mr-2" />
                  Thông tin tài chính
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-3">
                  <div className="flex justify-between">
                    <span className="text-sm text-muted-foreground">Tiền thuê hàng tháng:</span>
                    <span className="font-medium">{contract.monthlyRent.toLocaleString()} VND</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-sm text-muted-foreground">Tiền cọc:</span>
                    <span className="font-medium">{contract.deposit.toLocaleString()} VND</span>
                  </div>
                  <div className="flex justify-between border-t pt-2">
                    <span className="text-sm text-muted-foreground">Tổng giá trị hợp đồng:</span>
                    <span className="font-bold text-lg">
                      {(contract.monthlyRent * 12 + contract.deposit).toLocaleString()} VND
                    </span>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Contract Duration */}
          <Card>
            <CardHeader>
              <CardTitle className="text-lg flex items-center">
                <Calendar className="h-5 w-5 mr-2" />
                Thời hạn hợp đồng
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div>
                  <div className="text-sm text-muted-foreground">Ngày bắt đầu</div>
                  <div className="font-medium">{new Date(contract.startDate).toLocaleDateString("vi-VN")}</div>
                </div>
                <div>
                  <div className="text-sm text-muted-foreground">Ngày kết thúc</div>
                  <div className="font-medium">{new Date(contract.endDate).toLocaleDateString("vi-VN")}</div>
                </div>
                <div>
                  <div className="text-sm text-muted-foreground">Thời gian thuê</div>
                  <div className="font-medium">
                    {Math.ceil(
                      (new Date(contract.endDate).getTime() - new Date(contract.startDate).getTime()) /
                        (1000 * 60 * 60 * 24 * 30),
                    )}{" "}
                    tháng
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Payment Status */}
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Tình trạng thanh toán</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="flex items-center space-x-2">
                {contract.paymentStatus === "current" && (
                  <>
                    <CheckCircle className="h-5 w-5 text-green-600" />
                    <span className="text-green-600 font-medium">Thanh toán đầy đủ, đúng hạn</span>
                  </>
                )}
                {contract.paymentStatus === "overdue" && (
                  <>
                    <XCircle className="h-5 w-5 text-red-600" />
                    <span className="text-red-600 font-medium">Có khoản thanh toán quá hạn</span>
                  </>
                )}
                {contract.paymentStatus === "completed" && (
                  <>
                    <CheckCircle className="h-5 w-5 text-blue-600" />
                    <span className="text-blue-600 font-medium">Đã hoàn thành tất cả thanh toán</span>
                  </>
                )}
                {contract.paymentStatus === "pending_refund" && (
                  <>
                    <Clock className="h-5 w-5 text-orange-600" />
                    <span className="text-orange-600 font-medium">Chờ hoàn trả tiền cọc</span>
                  </>
                )}
              </div>
            </CardContent>
          </Card>

          {/* Actions */}
          <div className="flex justify-end space-x-2 pt-4 border-t">
            <Button variant="outline" onClick={() => onOpenChange(false)}>
              Đóng
            </Button>
            <Button variant="outline">
              <FileText className="mr-2 h-4 w-4" />
              Tải hợp đồng
            </Button>
            {contract.status === "active" && <Button variant="destructive">Chấm dứt hợp đồng</Button>}
          </div>
        </div>
      </DialogContent>
    </Dialog>
  )
}
