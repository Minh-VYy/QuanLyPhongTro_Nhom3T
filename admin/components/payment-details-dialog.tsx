"use client"

import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Avatar, AvatarFallback } from "@/components/ui/avatar"
import { Calendar, DollarSign, CreditCard, User, CheckCircle, Clock, AlertTriangle, FileText } from "lucide-react"

interface PaymentDetailsDialogProps {
  payment: any
  open: boolean
  onOpenChange: (open: boolean) => void
}

export function PaymentDetailsDialog({ payment, open, onOpenChange }: PaymentDetailsDialogProps) {
  if (!payment) return null

  const getStatusBadge = (status: string) => {
    switch (status) {
      case "paid":
        return (
          <Badge variant="default" className="flex items-center space-x-1">
            <CheckCircle className="h-3 w-3" />
            <span>Đã thanh toán</span>
          </Badge>
        )
      case "pending":
        return (
          <Badge variant="secondary" className="flex items-center space-x-1">
            <Clock className="h-3 w-3" />
            <span>Chờ thanh toán</span>
          </Badge>
        )
      case "overdue":
        return (
          <Badge variant="destructive" className="flex items-center space-x-1">
            <AlertTriangle className="h-3 w-3" />
            <span>Quá hạn</span>
          </Badge>
        )
      case "pending_refund":
        return (
          <Badge variant="outline" className="flex items-center space-x-1">
            <DollarSign className="h-3 w-3" />
            <span>Chờ hoàn trả</span>
          </Badge>
        )
      default:
        return <Badge variant="outline">Không xác định</Badge>
    }
  }

  const getPaymentTypeName = (type: string) => {
    switch (type) {
      case "monthly_rent":
        return "Tiền thuê hàng tháng"
      case "deposit":
        return "Tiền cọc"
      case "deposit_refund":
        return "Hoàn trả cọc"
      default:
        return "Khác"
    }
  }

  const getPaymentMethodName = (method: string | null) => {
    if (!method) return "Chưa xác định"
    switch (method) {
      case "bank_transfer":
        return "Chuyển khoản ngân hàng"
      case "cash":
        return "Tiền mặt"
      case "credit_card":
        return "Thẻ tín dụng"
      default:
        return method
    }
  }

  const calculateOverdueDays = () => {
    if (payment.status !== "overdue") return 0
    const dueDate = new Date(payment.dueDate)
    const today = new Date()
    const diffTime = today.getTime() - dueDate.getTime()
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
    return diffDays
  }

  const overdueDays = calculateOverdueDays()

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-2xl max-h-[80vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Chi tiết thanh toán</DialogTitle>
          <DialogDescription>Thông tin chi tiết về khoản thanh toán</DialogDescription>
        </DialogHeader>

        <div className="space-y-6">
          {/* Payment Header */}
          <Card>
            <CardHeader>
              <div className="flex items-start justify-between">
                <div>
                  <CardTitle className="text-xl flex items-center space-x-2">
                    <DollarSign className="h-6 w-6" />
                    <span>Thanh toán {payment.id}</span>
                  </CardTitle>
                  <div className="flex items-center space-x-4 mt-2">
                    {getStatusBadge(payment.status)}
                    {payment.status === "overdue" && overdueDays > 0 && (
                      <Badge variant="destructive">Quá hạn {overdueDays} ngày</Badge>
                    )}
                  </div>
                </div>
                <div className="text-right">
                  <div className="text-2xl font-bold">{payment.amount.toLocaleString()} VND</div>
                  <div className="text-sm text-muted-foreground">{getPaymentTypeName(payment.type)}</div>
                </div>
              </div>
            </CardHeader>
          </Card>

          {/* Payer Info */}
          <Card>
            <CardHeader>
              <CardTitle className="text-lg flex items-center">
                <User className="h-5 w-5 mr-2" />
                Thông tin người thanh toán
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="flex items-center space-x-3">
                <Avatar className="h-12 w-12">
                  <AvatarFallback className="text-lg">{payment.tenant.charAt(0)}</AvatarFallback>
                </Avatar>
                <div>
                  <div className="font-medium text-lg">{payment.tenant}</div>
                  <div className="text-sm text-muted-foreground">{payment.property}</div>
                  <div className="text-sm text-muted-foreground">Hợp đồng: {payment.contractId}</div>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Payment Details */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Card>
              <CardHeader>
                <CardTitle className="text-lg flex items-center">
                  <Calendar className="h-5 w-5 mr-2" />
                  Thời gian
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-3">
                  <div>
                    <div className="text-sm text-muted-foreground">Hạn thanh toán</div>
                    <div className="font-medium">{new Date(payment.dueDate).toLocaleDateString("vi-VN")}</div>
                  </div>
                  {payment.paidDate && (
                    <div>
                      <div className="text-sm text-muted-foreground">Ngày thanh toán</div>
                      <div className="font-medium">{new Date(payment.paidDate).toLocaleDateString("vi-VN")}</div>
                    </div>
                  )}
                  {payment.month && (
                    <div>
                      <div className="text-sm text-muted-foreground">Tháng</div>
                      <div className="font-medium">{payment.month}</div>
                    </div>
                  )}
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle className="text-lg flex items-center">
                  <CreditCard className="h-5 w-5 mr-2" />
                  Phương thức thanh toán
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-3">
                  <div>
                    <div className="text-sm text-muted-foreground">Phương thức</div>
                    <div className="font-medium">{getPaymentMethodName(payment.method)}</div>
                  </div>
                  {payment.status === "paid" && (
                    <div className="flex items-center space-x-2 text-green-600">
                      <CheckCircle className="h-4 w-4" />
                      <span className="text-sm font-medium">Thanh toán thành công</span>
                    </div>
                  )}
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Recipient Info */}
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Người nhận thanh toán</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="flex items-center space-x-3">
                <Avatar className="h-10 w-10">
                  <AvatarFallback>{payment.landlord.charAt(0)}</AvatarFallback>
                </Avatar>
                <div>
                  <div className="font-medium">{payment.landlord}</div>
                  <div className="text-sm text-muted-foreground">Chủ trọ</div>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Payment Status Details */}
          {payment.status === "overdue" && (
            <Card className="border-red-200 dark:border-red-800">
              <CardHeader>
                <CardTitle className="text-lg text-red-600 dark:text-red-400 flex items-center">
                  <AlertTriangle className="h-5 w-5 mr-2" />
                  Thanh toán quá hạn
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="text-sm text-red-700 dark:text-red-300">
                  <p>Khoản thanh toán này đã quá hạn {overdueDays} ngày.</p>
                  <p className="mt-1">Vui lòng liên hệ với người thuê để xử lý thanh toán.</p>
                </div>
              </CardContent>
            </Card>
          )}

          {payment.status === "pending_refund" && (
            <Card className="border-orange-200 dark:border-orange-800">
              <CardHeader>
                <CardTitle className="text-lg text-orange-600 dark:text-orange-400 flex items-center">
                  <Clock className="h-5 w-5 mr-2" />
                  Chờ hoàn trả
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="text-sm text-orange-700 dark:text-orange-300">
                  <p>Khoản tiền cọc này đang chờ được hoàn trả cho người thuê.</p>
                  <p className="mt-1">Vui lòng xử lý hoàn trả trong thời gian sớm nhất.</p>
                </div>
              </CardContent>
            </Card>
          )}

          {/* Actions */}
          <div className="flex justify-end space-x-2 pt-4 border-t">
            <Button variant="outline" onClick={() => onOpenChange(false)}>
              Đóng
            </Button>
            <Button variant="outline">
              <FileText className="mr-2 h-4 w-4" />
              Tải hóa đơn
            </Button>
            {payment.status === "pending" && <Button>Xác nhận thanh toán</Button>}
            {payment.status === "pending_refund" && <Button>Xử lý hoàn trả</Button>}
          </div>
        </div>
      </DialogContent>
    </Dialog>
  )
}
