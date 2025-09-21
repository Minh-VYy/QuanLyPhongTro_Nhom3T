"use client"

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Users, Home, FileText, AlertTriangle, DollarSign, Eye } from "lucide-react"
import { StatsCard } from "@/components/stats-card"
import { RecentActivity } from "@/components/recent-activity"
import { MonthlyChart } from "@/components/monthly-chart"
import { UserDistributionChart } from "@/components/user-distribution-chart"
import { RevenueChart } from "@/components/revenue-chart"

export default function DashboardPage() {
  // Mock data - in real app, this would come from API
  const stats = {
    activeTenants: 1247,
    totalLandlords: 342,
    totalProperties: 856,
    activeContracts: 1089,
    pendingReports: 23,
    monthlyRevenue: 125000000, // VND
  }

  const recentActivities = [
    {
      id: 1,
      type: "new_property",
      message: "Bài đăng mới cần duyệt: Phòng trọ Quận 1",
      time: "5 phút trước",
      status: "pending",
    },
    {
      id: 2,
      type: "report",
      message: "Báo cáo vi phạm từ người thuê",
      time: "15 phút trước",
      status: "urgent",
    },
    {
      id: 3,
      type: "contract",
      message: "Hợp đồng mới được ký kết",
      time: "1 giờ trước",
      status: "success",
    },
    {
      id: 4,
      type: "payment",
      message: "Thanh toán thành công 5.000.000 VND",
      time: "2 giờ trước",
      status: "success",
    },
  ]

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div>
        <h1 className="text-2xl font-bold text-foreground">Dashboard</h1>
        <p className="text-muted-foreground">Tổng quan hệ thống quản lý phòng trọ</p>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <StatsCard
          title="Người thuê đang hoạt động"
          value={stats.activeTenants.toLocaleString()}
          change={+12.5}
          icon={Users}
          description="So với tháng trước"
        />
        <StatsCard
          title="Người cho thuê"
          value={stats.totalLandlords.toLocaleString()}
          change={+8.2}
          icon={Users}
          description="Tổng số chủ trọ"
        />
        <StatsCard
          title="Phòng trọ được đăng"
          value={stats.totalProperties.toLocaleString()}
          change={+15.3}
          icon={Home}
          description="Tổng số bài đăng"
        />
        <StatsCard
          title="Hợp đồng hiệu lực"
          value={stats.activeContracts.toLocaleString()}
          change={+5.7}
          icon={FileText}
          description="Đang hoạt động"
        />
        <StatsCard
          title="Báo cáo cần xử lý"
          value={stats.pendingReports.toLocaleString()}
          change={-2.1}
          icon={AlertTriangle}
          description="Giảm so với tuần trước"
          variant="warning"
        />
        <StatsCard
          title="Doanh thu tháng"
          value={`${(stats.monthlyRevenue / 1000000).toFixed(0)}M VND`}
          change={+18.9}
          icon={DollarSign}
          description="Phí dịch vụ & quảng cáo"
        />
      </div>

      {/* Charts Section */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card>
          <CardHeader>
            <CardTitle>Phòng đăng mới theo tháng</CardTitle>
            <CardDescription>Thống kê số lượng bài đăng mới trong 6 tháng gần đây</CardDescription>
          </CardHeader>
          <CardContent>
            <MonthlyChart />
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Tỷ lệ người dùng</CardTitle>
            <CardDescription>Phân bố người thuê và người cho thuê</CardDescription>
          </CardHeader>
          <CardContent>
            <UserDistributionChart />
          </CardContent>
        </Card>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Revenue Chart */}
        <Card className="lg:col-span-2">
          <CardHeader>
            <CardTitle>Doanh thu theo thời gian</CardTitle>
            <CardDescription>Biểu đồ doanh thu 12 tháng gần đây</CardDescription>
          </CardHeader>
          <CardContent>
            <RevenueChart />
          </CardContent>
        </Card>

        {/* Recent Activity */}
        <Card>
          <CardHeader>
            <CardTitle>Hoạt động gần đây</CardTitle>
            <CardDescription>Các sự kiện và thông báo mới nhất</CardDescription>
          </CardHeader>
          <CardContent>
            <RecentActivity activities={recentActivities} />
          </CardContent>
        </Card>
      </div>

      {/* Quick Actions */}
      <Card>
        <CardHeader>
          <CardTitle>Thao tác nhanh</CardTitle>
          <CardDescription>Các tác vụ thường dùng</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <Button variant="outline" className="h-20 flex flex-col space-y-2 bg-transparent">
              <Eye className="h-5 w-5" />
              <span className="text-sm">Duyệt tin mới</span>
              <Badge variant="secondary" className="text-xs">
                12 tin
              </Badge>
            </Button>
            <Button variant="outline" className="h-20 flex flex-col space-y-2 bg-transparent">
              <AlertTriangle className="h-5 w-5" />
              <span className="text-sm">Xử lý báo cáo</span>
              <Badge variant="destructive" className="text-xs">
                {stats.pendingReports} báo cáo
              </Badge>
            </Button>
            <Button variant="outline" className="h-20 flex flex-col space-y-2 bg-transparent">
              <Users className="h-5 w-5" />
              <span className="text-sm">Quản lý user</span>
            </Button>
            <Button variant="outline" className="h-20 flex flex-col space-y-2 bg-transparent">
              <FileText className="h-5 w-5" />
              <span className="text-sm">Hợp đồng mới</span>
              <Badge variant="secondary" className="text-xs">
                8 hợp đồng
              </Badge>
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
