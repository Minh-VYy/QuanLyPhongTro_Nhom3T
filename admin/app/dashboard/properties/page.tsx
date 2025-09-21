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
import { Search, MoreHorizontal, Eye, Check, X, MapPin, DollarSign, Calendar } from "lucide-react"
import { PropertyDetailsDialog } from "@/components/property-details-dialog"
import { ApprovalDialog } from "@/components/approval-dialog"

// Mock data
const properties = [
  {
    id: 1,
    title: "Phòng trọ cao cấp Quận 1",
    landlord: "Nguyễn Văn An",
    landlordId: 1,
    price: 8000000,
    address: "123 Nguyễn Huệ, Quận 1, TP.HCM",
    status: "pending",
    createdDate: "2024-12-20",
    images: ["/placeholder.svg?height=200&width=300"],
    description: "Phòng trọ đầy đủ tiện nghi, gần trung tâm thành phố",
    area: 25,
    utilities: ["Wifi", "Điều hòa", "Tủ lạnh", "Máy giặt"],
  },
  {
    id: 2,
    title: "Căn hộ mini Quận 3",
    landlord: "Trần Thị Bình",
    landlordId: 2,
    price: 6500000,
    address: "456 Võ Văn Tần, Quận 3, TP.HCM",
    status: "approved",
    createdDate: "2024-12-19",
    images: ["/placeholder.svg?height=200&width=300"],
    description: "Căn hộ mini thoáng mát, an ninh tốt",
    area: 30,
    utilities: ["Wifi", "Điều hòa", "Bếp gas"],
  },
  {
    id: 3,
    title: "Phòng trọ sinh viên Quận 7",
    landlord: "Lê Minh Cường",
    landlordId: 3,
    price: 3500000,
    address: "789 Nguyễn Thị Thập, Quận 7, TP.HCM",
    status: "rejected",
    createdDate: "2024-12-18",
    images: ["/placeholder.svg?height=200&width=300"],
    description: "Phòng trọ giá rẻ cho sinh viên",
    area: 20,
    utilities: ["Wifi", "Quạt trần"],
    rejectionReason: "Hình ảnh không rõ ràng, thiếu thông tin tiện ích",
  },
  {
    id: 4,
    title: "Nhà nguyên căn Quận 2",
    landlord: "Phạm Thị Dung",
    landlordId: 4,
    price: 15000000,
    address: "321 Đỗ Xuân Hợp, Quận 2, TP.HCM",
    status: "pending",
    createdDate: "2024-12-17",
    images: ["/placeholder.svg?height=200&width=300"],
    description: "Nhà nguyên căn 2 phòng ngủ, có sân vườn",
    area: 80,
    utilities: ["Wifi", "Điều hòa", "Tủ lạnh", "Máy giặt", "Bếp gas", "Sân vườn"],
  },
  {
    id: 5,
    title: "Studio apartment Quận 1",
    landlord: "Hoàng Văn Em",
    landlordId: 5,
    price: 12000000,
    address: "654 Lê Lợi, Quận 1, TP.HCM",
    status: "approved",
    createdDate: "2024-12-16",
    images: ["/placeholder.svg?height=200&width=300"],
    description: "Studio hiện đại, view đẹp",
    area: 35,
    utilities: ["Wifi", "Điều hòa", "Tủ lạnh", "Bếp từ"],
  },
]

export default function PropertiesPage() {
  const [searchTerm, setSearchTerm] = useState("")
  const [statusFilter, setStatusFilter] = useState("all")
  const [selectedProperty, setSelectedProperty] = useState<any>(null)
  const [approvalProperty, setApprovalProperty] = useState<any>(null)
  const [approvalAction, setApprovalAction] = useState<"approve" | "reject">("approve")

  const filteredProperties = properties.filter((property) => {
    const matchesSearch =
      property.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
      property.landlord.toLowerCase().includes(searchTerm.toLowerCase()) ||
      property.address.toLowerCase().includes(searchTerm.toLowerCase())

    const matchesStatus = statusFilter === "all" || property.status === statusFilter

    return matchesSearch && matchesStatus
  })

  const handleViewDetails = (property: any) => {
    setSelectedProperty(property)
  }

  const handleApproval = (property: any, action: "approve" | "reject") => {
    setApprovalProperty(property)
    setApprovalAction(action)
  }

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

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div>
        <h1 className="text-2xl font-bold text-foreground">Quản lý phòng trọ</h1>
        <p className="text-muted-foreground">Duyệt và quản lý bài đăng phòng trọ</p>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Tổng bài đăng</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{properties.length}</div>
            <p className="text-xs text-muted-foreground">+15% so với tháng trước</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Chờ duyệt</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{properties.filter((p) => p.status === "pending").length}</div>
            <p className="text-xs text-muted-foreground">Cần xử lý</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Đã duyệt</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{properties.filter((p) => p.status === "approved").length}</div>
            <p className="text-xs text-muted-foreground">Đang hiển thị</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Bị từ chối</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{properties.filter((p) => p.status === "rejected").length}</div>
            <p className="text-xs text-muted-foreground">Cần chỉnh sửa</p>
          </CardContent>
        </Card>
      </div>

      {/* Search and Filters */}
      <Card>
        <CardHeader>
          <CardTitle>Danh sách bài đăng</CardTitle>
          <CardDescription>Quản lý và duyệt các bài đăng phòng trọ</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="flex items-center space-x-2 mb-4">
            <div className="relative flex-1">
              <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
              <Input
                placeholder="Tìm kiếm theo tiêu đề, chủ trọ hoặc địa chỉ..."
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
                <SelectItem value="pending">Chờ duyệt</SelectItem>
                <SelectItem value="approved">Đã duyệt</SelectItem>
                <SelectItem value="rejected">Bị từ chối</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <div className="rounded-md border">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Bài đăng</TableHead>
                  <TableHead>Chủ trọ</TableHead>
                  <TableHead>Giá thuê</TableHead>
                  <TableHead>Địa chỉ</TableHead>
                  <TableHead>Trạng thái</TableHead>
                  <TableHead>Ngày đăng</TableHead>
                  <TableHead className="text-right">Thao tác</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {filteredProperties.map((property) => (
                  <TableRow key={property.id}>
                    <TableCell>
                      <div className="flex items-center space-x-3">
                        <img
                          src={property.images[0] || "/placeholder.svg"}
                          alt={property.title}
                          className="h-12 w-16 rounded object-cover"
                        />
                        <div>
                          <div className="font-medium">{property.title}</div>
                          <div className="text-sm text-muted-foreground">ID: {property.id}</div>
                        </div>
                      </div>
                    </TableCell>
                    <TableCell>
                      <div className="flex items-center space-x-2">
                        <Avatar className="h-6 w-6">
                          <AvatarFallback className="text-xs">{property.landlord.charAt(0)}</AvatarFallback>
                        </Avatar>
                        <span className="text-sm">{property.landlord}</span>
                      </div>
                    </TableCell>
                    <TableCell>
                      <div className="flex items-center">
                        <DollarSign className="h-4 w-4 mr-1 text-muted-foreground" />
                        <span className="font-medium">{(property.price / 1000000).toFixed(1)}M</span>
                      </div>
                    </TableCell>
                    <TableCell>
                      <div className="flex items-center text-sm">
                        <MapPin className="h-3 w-3 mr-1 text-muted-foreground" />
                        <span className="truncate max-w-40">{property.address}</span>
                      </div>
                    </TableCell>
                    <TableCell>{getStatusBadge(property.status)}</TableCell>
                    <TableCell>
                      <div className="flex items-center text-sm">
                        <Calendar className="h-3 w-3 mr-1 text-muted-foreground" />
                        {new Date(property.createdDate).toLocaleDateString("vi-VN")}
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
                          <DropdownMenuItem onClick={() => handleViewDetails(property)}>
                            <Eye className="mr-2 h-4 w-4" />
                            Xem chi tiết
                          </DropdownMenuItem>
                          {property.status === "pending" && (
                            <>
                              <DropdownMenuSeparator />
                              <DropdownMenuItem onClick={() => handleApproval(property, "approve")}>
                                <Check className="mr-2 h-4 w-4" />
                                Duyệt tin
                              </DropdownMenuItem>
                              <DropdownMenuItem onClick={() => handleApproval(property, "reject")}>
                                <X className="mr-2 h-4 w-4" />
                                Từ chối
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

      {/* Property Details Dialog */}
      {selectedProperty && (
        <PropertyDetailsDialog
          property={selectedProperty}
          open={!!selectedProperty}
          onOpenChange={(open) => !open && setSelectedProperty(null)}
        />
      )}

      {/* Approval Dialog */}
      {approvalProperty && (
        <ApprovalDialog
          property={approvalProperty}
          action={approvalAction}
          open={!!approvalProperty}
          onOpenChange={(open) => !open && setApprovalProperty(null)}
          onConfirm={(reason) => {
            // TODO: Implement approval/rejection logic
            console.log(`${approvalAction} property ${approvalProperty.id}:`, reason)
            setApprovalProperty(null)
          }}
        />
      )}
    </div>
  )
}
