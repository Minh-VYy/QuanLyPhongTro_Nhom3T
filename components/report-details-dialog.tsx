"use client"

import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Avatar, AvatarFallback } from "@/components/ui/avatar"
import { Calendar, User, Home, AlertTriangle, CheckCircle, Clock, MessageSquare } from "lucide-react"

interface ReportDetailsDialogProps {
  report: any
  open: boolean
  onOpenChange: (open: boolean) => void
}

export function ReportDetailsDialog({ report, open, onOpenChange }: ReportDetailsDialogProps) {
  if (!report) return null

  const getStatusBadge = (status: string) => {
    switch (status) {
      case "pending":
        return (
          <Badge variant="secondary" className="flex items-center space-x-1">
            <Clock className="h-3 w-3" />
            <span>Chưa xử lý</span>
          </Badge>
        )
      case "in_progress":
        return (
          <Badge variant="default" className="flex items-center space-x-1">
            <AlertTriangle className="h-3 w-3" />
            <span>Đang xử lý</span>
          </Badge>
        )
      case "resolved":
        return (
          <Badge variant="outline" className="flex items-center space-x-1 text-green-600 border-green-600">
            <CheckCircle className="h-3 w-3" />
            <span>Đã xử lý</span>
          </Badge>
        )
      default:
        return <Badge variant="outline">Không xác định</Badge>
    }
  }

  const getPriorityBadge = (priority: string) => {
    switch (priority) {
      case "high":
        return <Badge variant="destructive">Cao</Badge>
      case "medium":
        return <Badge variant="secondary">Trung bình</Badge>
      case "low":
        return <Badge variant="outline">Thấp</Badge>
      default:
        return <Badge variant="outline">Không xác định</Badge>
    }
  }

  const getCategoryName = (category: string) => {
    switch (category) {
      case "misleading_info":
        return "Thông tin sai lệch"
      case "financial_dispute":
        return "Tranh chấp tài chính"
      case "behavior_issue":
        return "Vấn đề hành vi"
      case "inappropriate_content":
        return "Nội dung không phù hợp"
      case "fraud":
        return "Lừa đảo"
      default:
        return "Khác"
    }
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-4xl max-h-[80vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Chi tiết báo cáo</DialogTitle>
          <DialogDescription>Thông tin chi tiết về báo cáo và khiếu nại</DialogDescription>
        </DialogHeader>

        <div className="space-y-6">
          {/* Report Header */}
          <Card>
            <CardHeader>
              <div className="flex items-start justify-between">
                <div>
                  <CardTitle className="text-xl">{report.title}</CardTitle>
                  <div className="flex items-center space-x-4 mt-2">
                    <Badge variant="outline">{getCategoryName(report.category)}</Badge>
                    {getPriorityBadge(report.priority)}
                    {getStatusBadge(report.status)}
                  </div>
                </div>
                <div className="text-sm text-muted-foreground">
                  <div className="flex items-center">
                    <Calendar className="h-4 w-4 mr-1" />
                    {new Date(report.createdDate).toLocaleString("vi-VN")}
                  </div>
                </div>
              </div>
            </CardHeader>
          </Card>

          {/* Involved Parties */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Card>
              <CardHeader>
                <CardTitle className="text-lg flex items-center">
                  <User className="h-5 w-5 mr-2" />
                  Người báo cáo
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="flex items-center space-x-3">
                  <Avatar className="h-10 w-10">
                    <AvatarFallback>{report.reporter.charAt(0)}</AvatarFallback>
                  </Avatar>
                  <div>
                    <div className="font-medium">{report.reporter}</div>
                    <div className="text-sm text-muted-foreground">
                      {report.reporterType === "tenant"
                        ? "Người thuê"
                        : report.reporterType === "landlord"
                          ? "Chủ trọ"
                          : "Người dùng"}
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle className="text-lg flex items-center">
                  <AlertTriangle className="h-5 w-5 mr-2" />
                  Đối tượng bị báo cáo
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="flex items-center space-x-3">
                  <Avatar className="h-10 w-10">
                    <AvatarFallback>{report.reportedUser.charAt(0)}</AvatarFallback>
                  </Avatar>
                  <div>
                    <div className="font-medium">{report.reportedUser}</div>
                    <div className="text-sm text-muted-foreground">
                      {report.reportedUserType === "tenant" ? "Người thuê" : "Chủ trọ"}
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Property Info */}
          <Card>
            <CardHeader>
              <CardTitle className="text-lg flex items-center">
                <Home className="h-5 w-5 mr-2" />
                Phòng liên quan
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="font-medium">{report.propertyTitle}</div>
              <div className="text-sm text-muted-foreground">ID: {report.propertyId}</div>
            </CardContent>
          </Card>

          {/* Report Description */}
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Nội dung báo cáo</CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-sm leading-relaxed">{report.description}</p>
            </CardContent>
          </Card>

          {/* Evidence Images */}
          {report.images && report.images.length > 0 && (
            <Card>
              <CardHeader>
                <CardTitle className="text-lg">Hình ảnh minh chứng</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
                  {report.images.map((image: string, index: number) => (
                    <img
                      key={index}
                      src={image || "/placeholder.svg?height=200&width=300"}
                      alt={`Minh chứng ${index + 1}`}
                      className="w-full h-32 object-cover rounded-lg border"
                    />
                  ))}
                </div>
              </CardContent>
            </Card>
          )}

          {/* Admin Response */}
          {report.response && (
            <Card>
              <CardHeader>
                <CardTitle className="text-lg flex items-center">
                  <MessageSquare className="h-5 w-5 mr-2" />
                  Phản hồi của Admin
                </CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-sm leading-relaxed">{report.response}</p>
              </CardContent>
            </Card>
          )}

          {/* Resolution */}
          {report.resolution && (
            <Card>
              <CardHeader>
                <CardTitle className="text-lg flex items-center">
                  <CheckCircle className="h-5 w-5 mr-2 text-green-600" />
                  Kết quả xử lý
                </CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-sm leading-relaxed">{report.resolution}</p>
              </CardContent>
            </Card>
          )}

          {/* Actions */}
          <div className="flex justify-end space-x-2 pt-4 border-t">
            <Button variant="outline" onClick={() => onOpenChange(false)}>
              Đóng
            </Button>
            {report.status !== "resolved" && (
              <Button>
                <MessageSquare className="mr-2 h-4 w-4" />
                Phản hồi
              </Button>
            )}
          </div>
        </div>
      </DialogContent>
    </Dialog>
  )
}
