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
import { Search, MoreHorizontal, Eye, Lock, Unlock, Home, Phone, Mail, Calendar } from "lucide-react"
import { UserDetailsDialog } from "@/components/user-details-dialog"

// Mock data
const landlords = [
  {
    id: 1,
    name: "Nguyễn Văn An",
    email: "nguyenvanan@email.com",
    phone: "0901234567",
    status: "active",
    joinDate: "2024-01-15",
    propertiesCount: 5,
    avatar: "/placeholder.svg?height=40&width=40",
    totalRevenue: 25000000,
    lastLogin: "2024-12-20",
  },
  {
    id: 2,
    name: "Trần Thị Bình",
    email: "tranthibinh@email.com",
    phone: "0912345678",
    status: "active",
    joinDate: "2024-02-20",
    propertiesCount: 3,
    avatar: "/placeholder.svg?height=40&width=40",
    totalRevenue: 18000000,
    lastLogin: "2024-12-19",
  },
  {
    id: 3,
    name: "Lê Minh Cường",
    email: "leminhcuong@email.com",
    phone: "0923456789",
    status: "locked",
    joinDate: "2024-03-10",
    propertiesCount: 2,
    avatar: "/placeholder.svg?height=40&width=40",
    totalRevenue: 12000000,
    lastLogin: "2024-12-15",
  },
  {
    id: 4,
    name: "Phạm Thị Dung",
    email: "phamthidung@email.com",
    phone: "0934567890",
    status: "active",
    joinDate: "2024-04-05",
    propertiesCount: 8,
    avatar: "/placeholder.svg?height=40&width=40",
    totalRevenue: 35000000,
    lastLogin: "2024-12-20",
  },
  {
    id: 5,
    name: "Hoàng Văn Em",
    email: "hoangvanem@email.com",
    phone: "0945678901",
    status: "active",
    joinDate: "2024-05-12",
    propertiesCount: 1,
    avatar: "/placeholder.svg?height=40&width=40",
    totalRevenue: 8000000,
    lastLogin: "2024-12-18",
  },
]

export default function LandlordsPage() {
  const [searchTerm, setSearchTerm] = useState("")
  const [selectedUser, setSelectedUser] = useState<any>(null)

  const filteredLandlords = landlords.filter(
    (landlord) =>
      landlord.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      landlord.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
      landlord.phone.includes(searchTerm),
  )

  const handleStatusToggle = (id: number, currentStatus: string) => {
    // TODO: Implement status toggle logic
    console.log(`Toggle status for landlord ${id}: ${currentStatus}`)
  }

  const handleViewDetails = (landlord: any) => {
    setSelectedUser(landlord)
  }

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div>
        <h1 className="text-2xl font-bold text-foreground">Quản lý người cho thuê</h1>
        <p className="text-muted-foreground">Danh sách và quản lý tài khoản chủ trọ</p>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Tổng số chủ trọ</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{landlords.length}</div>
            <p className="text-xs text-muted-foreground">+12% so với tháng trước</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Đang hoạt động</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{landlords.filter((l) => l.status === "active").length}</div>
            <p className="text-xs text-muted-foreground">Tài khoản hoạt động</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Bị khóa</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{landlords.filter((l) => l.status === "locked").length}</div>
            <p className="text-xs text-muted-foreground">Tài khoản bị khóa</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Tổng phòng</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{landlords.reduce((sum, l) => sum + l.propertiesCount, 0)}</div>
            <p className="text-xs text-muted-foreground">Phòng được đăng</p>
          </CardContent>
        </Card>
      </div>

      {/* Search and Filters */}
      <Card>
        <CardHeader>
          <CardTitle>Danh sách người cho thuê</CardTitle>
          <CardDescription>Quản lý thông tin và trạng thái tài khoản chủ trọ</CardDescription>
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
                  <TableHead>Số phòng</TableHead>
                  <TableHead>Doanh thu</TableHead>
                  <TableHead>Ngày tham gia</TableHead>
                  <TableHead className="text-right">Thao tác</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {filteredLandlords.map((landlord) => (
                  <TableRow key={landlord.id}>
                    <TableCell>
                      <div className="flex items-center space-x-3">
                        <Avatar className="h-8 w-8">
                          <AvatarImage src={landlord.avatar || "/placeholder.svg"} alt={landlord.name} />
                          <AvatarFallback>{landlord.name.charAt(0)}</AvatarFallback>
                        </Avatar>
                        <div>
                          <div className="font-medium">{landlord.name}</div>
                          <div className="text-sm text-muted-foreground">ID: {landlord.id}</div>
                        </div>
                      </div>
                    </TableCell>
                    <TableCell>
                      <div className="space-y-1">
                        <div className="flex items-center text-sm">
                          <Mail className="h-3 w-3 mr-1" />
                          {landlord.email}
                        </div>
                        <div className="flex items-center text-sm">
                          <Phone className="h-3 w-3 mr-1" />
                          {landlord.phone}
                        </div>
                      </div>
                    </TableCell>
                    <TableCell>
                      <Badge variant={landlord.status === "active" ? "default" : "destructive"}>
                        {landlord.status === "active" ? "Hoạt động" : "Bị khóa"}
                      </Badge>
                    </TableCell>
                    <TableCell>
                      <div className="flex items-center">
                        <Home className="h-4 w-4 mr-1" />
                        {landlord.propertiesCount}
                      </div>
                    </TableCell>
                    <TableCell>
                      <div className="font-medium">{(landlord.totalRevenue / 1000000).toFixed(1)}M VND</div>
                    </TableCell>
                    <TableCell>
                      <div className="flex items-center text-sm">
                        <Calendar className="h-3 w-3 mr-1" />
                        {new Date(landlord.joinDate).toLocaleDateString("vi-VN")}
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
                          <DropdownMenuItem onClick={() => handleViewDetails(landlord)}>
                            <Eye className="mr-2 h-4 w-4" />
                            Xem chi tiết
                          </DropdownMenuItem>
                          <DropdownMenuSeparator />
                          <DropdownMenuItem onClick={() => handleStatusToggle(landlord.id, landlord.status)}>
                            {landlord.status === "active" ? (
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
          userType="landlord"
          open={!!selectedUser}
          onOpenChange={(open) => !open && setSelectedUser(null)}
        />
      )}
    </div>
  )
}
