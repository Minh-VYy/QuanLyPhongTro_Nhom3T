"use client"

import { useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Avatar, AvatarFallback } from "@/components/ui/avatar"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import {
  Search,
  MoreHorizontal,
  Eye,
  FileText,
  DollarSign,
  CheckCircle,
  XCircle,
  Clock,
  AlertTriangle,
} from "lucide-react"
import { ContractDetailsDialog } from "@/components/contract-details-dialog"
import { PaymentDetailsDialog } from "@/components/payment-details-dialog"

// Mock data for contracts
const contracts = [
  {
    id: "CT001",
    tenant: "Nguyễn Thị Lan",
    tenantId: 1,
    landlord: "Trần Văn Minh",
    landlordId: 2,
    property: "Phòng trọ cao cấp Quận 1",
    propertyId: 1,
    monthlyRent: 8000000,
    deposit: 16000000,
    startDate: "2024-01-01",
    endDate: "2024-12-31",
    status: "active",
    signedDate: "2023-12-15",
    paymentStatus: "current",
  },
  {
    id: "CT002",
    tenant: "Lê Văn Đức",
    tenantId: 3,
    landlord: "Phạm Thị Dung",
    landlordId: 4,
    property: "Căn hộ mini Quận 3",
    propertyId: 2,
    monthlyRent: 6500000,
    deposit: 13000000,
    startDate: "2024-02-01",
    endDate: "2025-01-31",
    status: "active",
    signedDate: "2024-01-20",
    paymentStatus: "overdue",
  },
  {
    id: "CT003",
    tenant: "Hoàng Thị Mai",
    tenantId: 5,
    landlord: "Nguyễn Văn An",
    landlordId: 1,
    property: "Studio apartment Quận 1",
    propertyId: 3,
    monthlyRent: 12000000,
    deposit: 24000000,
    startDate: "2023-06-01",
    endDate: "2024-05-31",
    status: "expired",
    signedDate: "2023-05-15",
    paymentStatus: "completed",
  },
  {
    id: "CT004",
    tenant: "Trần Văn Bình",
    tenantId: 6,
    landlord: "Lê Minh Cường",
    landlordId: 3,
    property: "Phòng trọ sinh viên Quận 7",
    propertyId: 4,
    monthlyRent: 3500000,
    deposit: 7000000,
    startDate: "2024-03-01",
    endDate: "2025-02-28",
    status: "terminated",
    signedDate: "2024-02-20",
    paymentStatus: "pending_refund",
  },
]

// Mock data for payments
const payments = [
  {
    id: "PAY001",
    contractId: "CT001",
    tenant: "Nguyễn Thị Lan",
    landlord: "Trần Văn Minh",
    property: "Phòng trọ cao cấp Quận 1",
    amount: 8000000,
    type: "monthly_rent",
    dueDate: "2024-12-01",
    paidDate: "2024-11-28",
    status: "paid",
    method: "bank_transfer",
    month: "12/2024",
  },
  {
    id: "PAY002",
    contractId: "CT002",
    tenant: "Lê Văn Đức",
    landlord: "Phạm Thị Dung",
    property: "Căn hộ mini Quận 3",
    amount: 6500000,
    type: "monthly_rent",
    dueDate: "2024-12-01",
    paidDate: null,
    status: "overdue",
    method: null,
    month: "12/2024",
  },
  {
    id: "PAY003",
    contractId: "CT001",
    tenant: "Nguyễn Thị Lan",
    landlord: "Trần Văn Minh",
    property: "Phòng trọ cao cấp Quận 1",
    amount: 16000000,
    type: "deposit",
    dueDate: "2023-12-15",
    paidDate: "2023-12-15",
    status: "paid",
    method: "cash",
    month: null,
  },
  {
    id: "PAY004",
    contractId: "CT004",
    tenant: "Trần Văn Bình",
    landlord: "Lê Minh Cường",
    property: "Phòng trọ sinh viên Quận 7",
    amount: 7000000,
    type: "deposit_refund",
    dueDate: "2024-12-20",
    paidDate: null,
    status: "pending",
    method: null,
    month: null,
  },
  {
    id: "PAY005",
    contractId: "CT003",
    tenant: "Hoàng Thị Mai",
    landlord: "Nguyễn Văn An",
    property: "Studio apartment Quận 1",
    amount: 12000000,
    type: "monthly_rent",
    dueDate: "2024-05-01",
    paidDate: "2024-04-30",
    status: "paid",
    method: "bank_transfer",
    month: "05/2024",
  },
]

export default function ContractsPage() {
  const [searchTerm, setSearchTerm] = useState("")
  const [contractStatusFilter, setContractStatusFilter] = useState("all")
  const [paymentStatusFilter, setPaymentStatusFilter] = useState("all")
  const [selectedContract, setSelectedContract] = useState<any>(null)
  const [selectedPayment, setSelectedPayment] = useState<any>(null)

  const filteredContracts = contracts.filter((contract) => {
    const matchesSearch =
      contract.tenant.toLowerCase().includes(searchTerm.toLowerCase()) ||
      contract.landlord.toLowerCase().includes(searchTerm.toLowerCase()) ||
      contract.property.toLowerCase().includes(searchTerm.toLowerCase()) ||
      contract.id.toLowerCase().includes(searchTerm.toLowerCase())

    const matchesStatus = contractStatusFilter === "all" || contract.status === contractStatusFilter

    return matchesSearch && matchesStatus
  })

  const filteredPayments = payments.filter((payment) => {
    const matchesSearch =
      payment.tenant.toLowerCase().includes(searchTerm.toLowerCase()) ||
      payment.landlord.toLowerCase().includes(searchTerm.toLowerCase()) ||
      payment.property.toLowerCase().includes(searchTerm.toLowerCase()) ||
      payment.id.toLowerCase().includes(searchTerm.toLowerCase())

    const matchesStatus = paymentStatusFilter === "all" || payment.status === paymentStatusFilter

    return matchesSearch && matchesStatus
  })

  const getContractStatusBadge = (status: string) => {
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

  const getPaymentStatusBadge = (status: string) => {
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

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div>
        <h1 className="text-2xl font-bold text-foreground">Hợp đồng & Thanh toán</h1>
        <p className="text-muted-foreground">Quản lý hợp đồng thuê và theo dõi thanh toán</p>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Tổng hợp đồng</CardTitle>
            <FileText className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{contracts.length}</div>
            <p className="text-xs text-muted-foreground">+3 hợp đồng mới tháng này</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Đang hiệu lực</CardTitle>
            <CheckCircle className="h-4 w-4 text-green-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-green-600">
              {contracts.filter((c) => c.status === "active").length}
            </div>
            <p className="text-xs text-muted-foreground">Hợp đồng hoạt động</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Thanh toán quá hạn</CardTitle>
            <AlertTriangle className="h-4 w-4 text-red-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-red-600">
              {payments.filter((p) => p.status === "overdue").length}
            </div>
            <p className="text-xs text-muted-foreground">Cần xử lý ngay</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Doanh thu tháng</CardTitle>
            <DollarSign className="h-4 w-4 text-blue-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              {(
                payments
                  .filter((p) => p.status === "paid" && p.type === "monthly_rent" && p.month === "12/2024")
                  .reduce((sum, p) => sum + p.amount, 0) / 1000000
              ).toFixed(0)}
              M
            </div>
            <p className="text-xs text-muted-foreground">VND từ tiền thuê</p>
          </CardContent>
        </Card>
      </div>

      {/* Tabs for Contracts and Payments */}
      <Tabs defaultValue="contracts" className="space-y-4">
        <TabsList>
          <TabsTrigger value="contracts">Hợp đồng</TabsTrigger>
          <TabsTrigger value="payments">Thanh toán</TabsTrigger>
        </TabsList>

        {/* Contracts Tab */}
        <TabsContent value="contracts">
          <Card>
            <CardHeader>
              <CardTitle>Danh sách hợp đồng</CardTitle>
              <CardDescription>Quản lý các hợp đồng thuê phòng</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="flex items-center space-x-2 mb-4">
                <div className="relative flex-1">
                  <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
                  <Input
                    placeholder="Tìm kiếm theo mã hợp đồng, người thuê, chủ trọ..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="pl-8"
                  />
                </div>
                <Select value={contractStatusFilter} onValueChange={setContractStatusFilter}>
                  <SelectTrigger className="w-40">
                    <SelectValue placeholder="Trạng thái" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="all">Tất cả</SelectItem>
                    <SelectItem value="active">Đang hiệu lực</SelectItem>
                    <SelectItem value="expired">Hết hạn</SelectItem>
                    <SelectItem value="terminated">Đã chấm dứt</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="rounded-md border">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Mã hợp đồng</TableHead>
                      <TableHead>Người thuê</TableHead>
                      <TableHead>Chủ trọ</TableHead>
                      <TableHead>Phòng</TableHead>
                      <TableHead>Giá thuê</TableHead>
                      <TableHead>Thời hạn</TableHead>
                      <TableHead>Trạng thái</TableHead>
                      <TableHead className="text-right">Thao tác</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {filteredContracts.map((contract) => (
                      <TableRow key={contract.id}>
                        <TableCell>
                          <div className="font-medium">{contract.id}</div>
                          <div className="text-sm text-muted-foreground">
                            Ký: {new Date(contract.signedDate).toLocaleDateString("vi-VN")}
                          </div>
                        </TableCell>
                        <TableCell>
                          <div className="flex items-center space-x-2">
                            <Avatar className="h-6 w-6">
                              <AvatarFallback className="text-xs">{contract.tenant.charAt(0)}</AvatarFallback>
                            </Avatar>
                            <span className="text-sm">{contract.tenant}</span>
                          </div>
                        </TableCell>
                        <TableCell>
                          <div className="flex items-center space-x-2">
                            <Avatar className="h-6 w-6">
                              <AvatarFallback className="text-xs">{contract.landlord.charAt(0)}</AvatarFallback>
                            </Avatar>
                            <span className="text-sm">{contract.landlord}</span>
                          </div>
                        </TableCell>
                        <TableCell>
                          <div className="text-sm">{contract.property}</div>
                        </TableCell>
                        <TableCell>
                          <div className="font-medium">{(contract.monthlyRent / 1000000).toFixed(1)}M VND</div>
                          <div className="text-xs text-muted-foreground">
                            Cọc: {(contract.deposit / 1000000).toFixed(1)}M
                          </div>
                        </TableCell>
                        <TableCell>
                          <div className="text-sm">
                            {new Date(contract.startDate).toLocaleDateString("vi-VN")} -{" "}
                            {new Date(contract.endDate).toLocaleDateString("vi-VN")}
                          </div>
                        </TableCell>
                        <TableCell>{getContractStatusBadge(contract.status)}</TableCell>
                        <TableCell className="text-right">
                          <DropdownMenu>
                            <DropdownMenuTrigger asChild>
                              <Button variant="ghost" className="h-8 w-8 p-0">
                                <MoreHorizontal className="h-4 w-4" />
                              </Button>
                            </DropdownMenuTrigger>
                            <DropdownMenuContent align="end">
                              <DropdownMenuLabel>Thao tác</DropdownMenuLabel>
                              <DropdownMenuItem onClick={() => setSelectedContract(contract)}>
                                <Eye className="mr-2 h-4 w-4" />
                                Xem chi tiết
                              </DropdownMenuItem>
                              <DropdownMenuSeparator />
                              <DropdownMenuItem>
                                <FileText className="mr-2 h-4 w-4" />
                                Tải hợp đồng
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
        </TabsContent>

        {/* Payments Tab */}
        <TabsContent value="payments">
          <Card>
            <CardHeader>
              <CardTitle>Danh sách thanh toán</CardTitle>
              <CardDescription>Theo dõi các khoản thanh toán và hóa đơn</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="flex items-center space-x-2 mb-4">
                <div className="relative flex-1">
                  <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
                  <Input
                    placeholder="Tìm kiếm theo mã thanh toán, người thuê..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="pl-8"
                  />
                </div>
                <Select value={paymentStatusFilter} onValueChange={setPaymentStatusFilter}>
                  <SelectTrigger className="w-40">
                    <SelectValue placeholder="Trạng thái" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="all">Tất cả</SelectItem>
                    <SelectItem value="paid">Đã thanh toán</SelectItem>
                    <SelectItem value="pending">Chờ thanh toán</SelectItem>
                    <SelectItem value="overdue">Quá hạn</SelectItem>
                    <SelectItem value="pending_refund">Chờ hoàn trả</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="rounded-md border">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Mã thanh toán</TableHead>
                      <TableHead>Người thuê</TableHead>
                      <TableHead>Loại thanh toán</TableHead>
                      <TableHead>Số tiền</TableHead>
                      <TableHead>Hạn thanh toán</TableHead>
                      <TableHead>Trạng thái</TableHead>
                      <TableHead className="text-right">Thao tác</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {filteredPayments.map((payment) => (
                      <TableRow key={payment.id}>
                        <TableCell>
                          <div className="font-medium">{payment.id}</div>
                          <div className="text-sm text-muted-foreground">HĐ: {payment.contractId}</div>
                        </TableCell>
                        <TableCell>
                          <div className="flex items-center space-x-2">
                            <Avatar className="h-6 w-6">
                              <AvatarFallback className="text-xs">{payment.tenant.charAt(0)}</AvatarFallback>
                            </Avatar>
                            <div>
                              <div className="text-sm font-medium">{payment.tenant}</div>
                              <div className="text-xs text-muted-foreground">{payment.property}</div>
                            </div>
                          </div>
                        </TableCell>
                        <TableCell>
                          <div className="text-sm">{getPaymentTypeName(payment.type)}</div>
                          {payment.month && <div className="text-xs text-muted-foreground">Tháng {payment.month}</div>}
                        </TableCell>
                        <TableCell>
                          <div className="font-medium">{(payment.amount / 1000000).toFixed(1)}M VND</div>
                        </TableCell>
                        <TableCell>
                          <div className="text-sm">{new Date(payment.dueDate).toLocaleDateString("vi-VN")}</div>
                          {payment.paidDate && (
                            <div className="text-xs text-muted-foreground">
                              Đã trả: {new Date(payment.paidDate).toLocaleDateString("vi-VN")}
                            </div>
                          )}
                        </TableCell>
                        <TableCell>{getPaymentStatusBadge(payment.status)}</TableCell>
                        <TableCell className="text-right">
                          <DropdownMenu>
                            <DropdownMenuTrigger asChild>
                              <Button variant="ghost" className="h-8 w-8 p-0">
                                <MoreHorizontal className="h-4 w-4" />
                              </Button>
                            </DropdownMenuTrigger>
                            <DropdownMenuContent align="end">
                              <DropdownMenuLabel>Thao tác</DropdownMenuLabel>
                              <DropdownMenuItem onClick={() => setSelectedPayment(payment)}>
                                <Eye className="mr-2 h-4 w-4" />
                                Xem chi tiết
                              </DropdownMenuItem>
                              <DropdownMenuSeparator />
                              <DropdownMenuItem>
                                <FileText className="mr-2 h-4 w-4" />
                                Tải hóa đơn
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
        </TabsContent>
      </Tabs>

      {/* Contract Details Dialog */}
      {selectedContract && (
        <ContractDetailsDialog
          contract={selectedContract}
          open={!!selectedContract}
          onOpenChange={(open) => !open && setSelectedContract(null)}
        />
      )}

      {/* Payment Details Dialog */}
      {selectedPayment && (
        <PaymentDetailsDialog
          payment={selectedPayment}
          open={!!selectedPayment}
          onOpenChange={(open) => !open && setSelectedPayment(null)}
        />
      )}
    </div>
  )
}
