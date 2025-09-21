"use client"

import { useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { Search, MoreHorizontal, Eye, Lock, Unlock, FileText, Phone, Mail, Calendar } from "lucide-react"
import { UserDetailsDialog } from "@/components/user-details-dialog"

// Mock data
const tenants = [
  {
    id: 1,
    name: "Nguyễn Thị Lan",
    email: "nguyenthilan@email.com",
    phone: "0901234567",
    status: "active",
    joinDate: "2024-01-15",
    currentContract: "Phòng A101 - Quận 1",
    avatar: "/placeholder.svg?height=40&width=40",
    rentHistory: 3,
    lastLogin: "2024-12-20",
  },
  {
    id: 2,
    name: "Trần Văn Minh",
    email: "tranvanminh@email.com",
    phone: "0912345678",
    status: "active",
    joinDate: "2024-02-20",
    currentContract: "Phòng B205 - Quận 3",
    avatar: "/placeholder.svg?height=40&width=40",
    rentHistory: 2,
    lastLogin: "2024-12-19",
  },
  {
    id: 3,
    name: "Lê Thị Hoa",
    email: "lethihoa@email.com",
    phone: "0923456789",
    status: "locked",
    joinDate: "2024-03-10",
    currentContract: null,
    avatar: "/placeholder.svg?height=40&width=40",
    rentHistory: 1,
    lastLogin: "2024-12-15",
  },
  {
    id: 4,
    name: "Phạm Văn Đức",
    email: "phamvanduc@email.com",
    phone: "0934567890",
    status: "active",
    joinDate: "2024-04-05",
    currentContract: "Phòng C301 - Quận 7",
    avatar: "/placeholder.svg?height=40&width=40",
    rentHistory: 4,
    lastLogin: "2024-12-20",
  },
  {
    id: 5,
    name: "Hoàng Thị Mai",
    email: "hoangthimai@email.com",
    phone: "0945678901",
    status: "active",
    joinDate: "2024-05-12",
    currentContract: null,
    avatar: "/placeholder.svg?height=40&width=40",
    rentHistory: 0,
    lastLogin: "2024-12-18",
  },
]

export default function TenantsPage() {
  const [searchTerm, setSearchTerm] = useState("")
  const [selectedUser, setSelectedUser] = useState<any>(null)

  const filteredTenants = tenants.filter(
    (tenant) =>
      tenant.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      tenant.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
      tenant.phone.includes(searchTerm),
  )

  const handleStatusToggle = (id: number, currentStatus: string) => {
    // TODO: Implement status toggle logic
    console.log(`Toggle status for tenant ${id}: ${currentStatus}`)
  }

  const handleViewDetails = (tenant: any) => {
    setSelectedUser(tenant)
  }

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div>
        <h1 className="text-2xl font-bold text-foreground">Quản lý người thuê</h1>
        <p className="text-muted-foreground">Danh sách và quản lý tài khoản người thuê phòng</p>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Tổng số người thuê</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{tenants.length}</div>
            <p className="text-xs text-muted-foreground">+8% so với tháng trước</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Đang hoạt động</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{tenants.filter((t) => t.status === "active").length}</div>
            <p className="text-xs text-muted-foreground">Tài khoản hoạt động</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Đang thuê</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{tenants.filter((t) => t.currentContract).length}</div>
            <p className="text-xs text-muted-foreground">Có hợp đồng hiện tại</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Bị khóa</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{tenants.filter((t) => t.status === "locked").length}</div>
            <p className="text-xs text-muted-foreground">Tài khoản bị khóa</p>
          </CardContent>
        </Card>
      </div>

      {/* Search and Filters */}
      <Card>
        <CardHeader>
          <CardTitle>Danh sách người thuê</CardTitle>
          <CardDescription>Quản lý thông tin và trạng thái tài khoản người thuê</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="flex items-center space-x-2 mb-4">
            <div className="relative flex-1">
              <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
              <Input
                placeholder="Tìm kiếm theo tên, email hoặc số điện thoại..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-8"
              />
            </div>
            <Button variant="outline">Lọc</Button>
          </div>

          <div className="rounded-md border">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Người dùng</TableHead>
                  <TableHead>Liên hệ</TableHead>
                  <TableHead>Trạng thái</TableHead>
                  <TableHead>Hợp đồng hiện tại</TableHead>
                  <TableHead>Lịch sử thuê</TableHead>
                  <TableHead>Ngày tham gia</TableHead>
                  <TableHead className="text-right">Thao tác</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {filteredTenants.map((tenant) => (
                  <TableRow key={tenant.id}>
                    <TableCell>
                      <div className="flex items-center space-x-3">
                        <Avatar className="h-8 w-8">
                          <AvatarImage src={tenant.avatar || "/placeholder.svg"} alt={tenant.name} />
                          <AvatarFallback>{tenant.name.charAt(0)}</AvatarFallback>
                        </Avatar>
                        <div>
                          <div className="font-medium">{tenant.name}</div>
                          <div className="text-sm text-muted-foreground">ID: {tenant.id}</div>
                        </div>
                      </div>
                    </TableCell>
                    <TableCell>
                      <div className="space-y-1">
                        <div className="flex items-center text-sm">
                          <Mail className="h-3 w-3 mr-1" />
                          {tenant.email}
                        </div>
                        <div className="flex items-center text-sm">
                          <Phone className="h-3 w-3 mr-1" />
                          {tenant.phone}
                        </div>
                      </div>
                    </TableCell>
                    <TableCell>
                      <Badge variant={tenant.status === "active" ? "default" : "destructive"}>
                        {tenant.status === "active" ? "Hoạt động" : "Bị khóa"}
                      </Badge>
                    </TableCell>
                    <TableCell>
                      {tenant.currentContract ? (
                        <div className="text-sm">
                          <div className="font-medium">{tenant.currentContract}</div>
                          <Badge variant="secondary" className="text-xs mt-1">
                            Đang thuê
                          </Badge>
                        </div>
                      ) : (
                        <span className="text-muted-foreground text-sm">Không có</span>
                      )}
                    </TableCell>
                    <TableCell>
                      <div className="flex items-center">
                        <FileText className="h-4 w-4 mr-1" />
                        {tenant.rentHistory} lần
                      </div>
                    </TableCell>
                    <TableCell>
                      <div className="flex items-center text-sm">
                        <Calendar className="h-3 w-3 mr-1" />
                        {new Date(tenant.joinDate).toLocaleDateString("vi-VN")}
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
                          <DropdownMenuItem onClick={() => handleViewDetails(tenant)}>
                            <Eye className="mr-2 h-4 w-4" />
                            Xem chi tiết
                          </DropdownMenuItem>
                          <DropdownMenuSeparator />
                          <DropdownMenuItem onClick={() => handleStatusToggle(tenant.id, tenant.status)}>
                            {tenant.status === "active" ? (
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
                          </DropdownMenuItem>
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

      {/* User Details Dialog */}
      {selectedUser && (
        <UserDetailsDialog
          user={selectedUser}
          userType="tenant"
          open={!!selectedUser}
          onOpenChange={(open) => !open && setSelectedUser(null)}
        />
      )}
    </div>
  )
}
