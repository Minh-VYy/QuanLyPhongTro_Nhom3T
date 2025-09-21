"use client"

import { useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Avatar, AvatarFallback } from "@/components/ui/avatar"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Search, MoreHorizontal, Eye, MessageSquare, CheckCircle, AlertTriangle, Clock, User } from "lucide-react"
import { ReportDetailsDialog } from "@/components/report-details-dialog"
import { ReportResponseDialog } from "@/components/report-response-dialog"

// Mock data
const reports = [
  {
    id: 1,
    title: "Phòng không đúng mô tả",
    reporter: "Nguyễn Thị Lan",
    reporterType: "tenant",
    reportedUser: "Trần Văn Minh",
    reportedUserType: "landlord",
    propertyTitle: "Phòng trọ cao cấp Quận 1",
    propertyId: 1,
    category: "misleading_info",
    description: "Phòng thực tế không có điều hòa như trong mô tả, wifi rất yếu",
    status: "pending",
    priority: "medium",
    createdDate: "2024-12-20T10:30:00",
    images: ["/placeholder.svg?height=200&width=300"],
  },
  {
    id: 2,
    title: "Chủ trọ không trả lại tiền cọc",
    reporter: "Lê Văn Đức",
    reporterType: "tenant",
    reportedUser: "Phạm Thị Dung",
    reportedUserType: "landlord",
    propertyTitle: "Căn hộ mini Quận 3",
    propertyId: 2,
    category: "financial_dispute",
    description: "Đã kết thúc hợp đồng 1 tháng nhưng chủ trọ không trả lại tiền cọc 10 triệu",
    status: "in_progress",
    priority: "high",
    createdDate: "2024-12-19T14:15:00",
    response: "Đã liên hệ với chủ trọ để làm rõ vấn đề",
    images: [],
  },
  {
    id: 3,
    title: "Người thuê làm ồn, ảnh hưởng hàng xóm",
    reporter: "Hoàng Văn Em",
    reporterType: "landlord",
    reportedUser: "Trần Thị Mai",
    reportedUserType: "tenant",
    propertyTitle: "Studio apartment Quận 1",
    propertyId: 3,
    category: "behavior_issue",
    description: "Người thuê thường xuyên tổ chức tiệc tùng, làm ồn vào ban đêm",
    status: "resolved",
    priority: "medium",
    createdDate: "2024-12-18T09:45:00",
    response: "Đã cảnh cáo người thuê và cam kết không tái diễn",
    resolution: "Người thuê đã cam kết tuân thủ quy định và không tái diễn",
    images: [],
  },
  {
    id: 4,
    title: "Hình ảnh không phù hợp trong bài đăng",
    reporter: "Nguyễn Văn An",
    reporterType: "user",
    reportedUser: "Lê Minh Cường",
    reportedUserType: "landlord",
    propertyTitle: "Phòng trọ sinh viên Quận 7",
    propertyId: 4,
    category: "inappropriate_content",
    description: "Bài đăng chứa hình ảnh không phù hợp, không liên quan đến phòng trọ",
    status: "pending",
    priority: "low",
    createdDate: "2024-12-17T16:20:00",
    images: ["/placeholder.svg?height=200&width=300"],
  },
  {
    id: 5,
    title: "Lừa đảo tiền cọc",
    reporter: "Phạm Văn Bình",
    reporterType: "tenant",
    reportedUser: "Trần Thị Hoa",
    reportedUserType: "landlord",
    propertyTitle: "Nhà nguyên căn Quận 2",
    propertyId: 5,
    category: "fraud",
    description: "Yêu cầu chuyển tiền cọc trước khi xem phòng, sau đó mất liên lạc",
    status: "in_progress",
    priority: "high",
    createdDate: "2024-12-16T11:10:00",
    response: "Đang điều tra và liên hệ với cơ quan chức năng",
    images: [],
  },
]

export default function ReportsPage() {
  const [searchTerm, setSearchTerm] = useState("")
  const [statusFilter, setStatusFilter] = useState("all")
  const [priorityFilter, setPriorityFilter] = useState("all")
  const [selectedReport, setSelectedReport] = useState<any>(null)
  const [responseReport, setResponseReport] = useState<any>(null)

  const filteredReports = reports.filter((report) => {
    const matchesSearch =
      report.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
      report.reporter.toLowerCase().includes(searchTerm.toLowerCase()) ||
      report.reportedUser.toLowerCase().includes(searchTerm.toLowerCase()) ||
      report.propertyTitle.toLowerCase().includes(searchTerm.toLowerCase())

    const matchesStatus = statusFilter === "all" || report.status === statusFilter
    const matchesPriority = priorityFilter === "all" || report.priority === priorityFilter

    return matchesSearch && matchesStatus && matchesPriority
  })

  const handleViewDetails = (report: any) => {
    setSelectedReport(report)
  }

  const handleResponse = (report: any) => {
    setResponseReport(report)
  }

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
    <div className="space-y-6">
      {/* Page Header */}
      <div>
        <h1 className="text-2xl font-bold text-foreground">Báo cáo & Khiếu nại</h1>
        <p className="text-muted-foreground">Quản lý và xử lý các báo cáo từ người dùng</p>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Tổng báo cáo</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{reports.length}</div>
            <p className="text-xs text-muted-foreground">+5% so với tuần trước</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Chưa xử lý</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-orange-600">
              {reports.filter((r) => r.status === "pending").length}
            </div>
            <p className="text-xs text-muted-foreground">Cần xử lý ngay</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Đang xử lý</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-blue-600">
              {reports.filter((r) => r.status === "in_progress").length}
            </div>
            <p className="text-xs text-muted-foreground">Đang theo dõi</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Ưu tiên cao</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-red-600">{reports.filter((r) => r.priority === "high").length}</div>
            <p className="text-xs text-muted-foreground">Cần xử lý khẩn cấp</p>
          </CardContent>
        </Card>
      </div>

      {/* Search and Filters */}
      <Card>
        <CardHeader>
          <CardTitle>Danh sách báo cáo</CardTitle>
          <CardDescription>Quản lý và xử lý các báo cáo, khiếu nại từ người dùng</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="flex items-center space-x-2 mb-4">
            <div className="relative flex-1">
              <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
              <Input
                placeholder="Tìm kiếm theo tiêu đề, người báo cáo hoặc phòng..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-8"
              />
            </div>
            <Select value={statusFilter} onValueChange={setStatusFilter}>
              <SelectTrigger className="w-40">
                <SelectValue placeholder="Trạng thái" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Tất cả</SelectItem>
                <SelectItem value="pending">Chưa xử lý</SelectItem>
                <SelectItem value="in_progress">Đang xử lý</SelectItem>
                <SelectItem value="resolved">Đã xử lý</SelectItem>
              </SelectContent>
            </Select>
            <Select value={priorityFilter} onValueChange={setPriorityFilter}>
              <SelectTrigger className="w-40">
                <SelectValue placeholder="Ưu tiên" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Tất cả</SelectItem>
                <SelectItem value="high">Cao</SelectItem>
                <SelectItem value="medium">Trung bình</SelectItem>
                <SelectItem value="low">Thấp</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <div className="rounded-md border">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Báo cáo</TableHead>
                  <TableHead>Người báo cáo</TableHead>
                  <TableHead>Đối tượng</TableHead>
                  <TableHead>Phòng liên quan</TableHead>
                  <TableHead>Trạng thái</TableHead>
                  <TableHead>Ưu tiên</TableHead>
                  <TableHead>Ngày tạo</TableHead>
                  <TableHead className="text-right">Thao tác</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {filteredReports.map((report) => (
                  <TableRow key={report.id}>
                    <TableCell>
                      <div>
                        <div className="font-medium">{report.title}</div>
                        <div className="text-sm text-muted-foreground">{getCategoryName(report.category)}</div>
                      </div>
                    </TableCell>
                    <TableCell>
                      <div className="flex items-center space-x-2">
                        <Avatar className="h-6 w-6">
                          <AvatarFallback className="text-xs">{report.reporter.charAt(0)}</AvatarFallback>
                        </Avatar>
                        <div>
                          <div className="text-sm font-medium">{report.reporter}</div>
                          <div className="text-xs text-muted-foreground">
                            {report.reporterType === "tenant"
                              ? "Người thuê"
                              : report.reporterType === "landlord"
                                ? "Chủ trọ"
                                : "Người dùng"}
                          </div>
                        </div>
                      </div>
                    </TableCell>
                    <TableCell>
                      <div className="flex items-center space-x-2">
                        <User className="h-4 w-4 text-muted-foreground" />
                        <div>
                          <div className="text-sm font-medium">{report.reportedUser}</div>
                          <div className="text-xs text-muted-foreground">
                            {report.reportedUserType === "tenant" ? "Người thuê" : "Chủ trọ"}
                          </div>
                        </div>
                      </div>
                    </TableCell>
                    <TableCell>
                      <div className="text-sm">{report.propertyTitle}</div>
                    </TableCell>
                    <TableCell>{getStatusBadge(report.status)}</TableCell>
                    <TableCell>{getPriorityBadge(report.priority)}</TableCell>
                    <TableCell>
                      <div className="text-sm">{new Date(report.createdDate).toLocaleDateString("vi-VN")}</div>
                      <div className="text-xs text-muted-foreground">
                        {new Date(report.createdDate).toLocaleTimeString("vi-VN", {
                          hour: "2-digit",
                          minute: "2-digit",
                        })}
                      </div>
                    </TableCell>
                    <TableCell className="text-right">
                      <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                          <Button variant="ghost" className="h-8 w-8 p-0">
                            <MoreHorizontal className="h-4 w-4" />
                          </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end">
                          <DropdownMenuLabel>Thao tác</DropdownMenuLabel>
                          <DropdownMenuItem onClick={() => handleViewDetails(report)}>
                            <Eye className="mr-2 h-4 w-4" />
                            Xem chi tiết
                          </DropdownMenuItem>
                          {report.status !== "resolved" && (
                            <>
                              <DropdownMenuSeparator />
                              <DropdownMenuItem onClick={() => handleResponse(report)}>
                                <MessageSquare className="mr-2 h-4 w-4" />
                                Phản hồi
                              </DropdownMenuItem>
                            </>
                          )}
                        </DropdownMenuContent>
                      </DropdownMenu>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>
        </CardContent>
      </Card>

      {/* Report Details Dialog */}
      {selectedReport && (
        <ReportDetailsDialog
          report={selectedReport}
          open={!!selectedReport}
          onOpenChange={(open) => !open && setSelectedReport(null)}
        />
      )}

      {/* Report Response Dialog */}
      {responseReport && (
        <ReportResponseDialog
          report={responseReport}
          open={!!responseReport}
          onOpenChange={(open) => !open && setResponseReport(null)}
          onSubmit={(response, status) => {
            // TODO: Implement response submission logic
            console.log(`Response to report ${responseReport.id}:`, { response, status })
            setResponseReport(null)
          }}
        />
      )}
    </div>
  )
}
