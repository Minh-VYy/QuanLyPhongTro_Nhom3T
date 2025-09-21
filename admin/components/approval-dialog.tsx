"use client"

import { useState } from "react"
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Textarea } from "@/components/ui/textarea"
import { Label } from "@/components/ui/label"
import { Check, X } from "lucide-react"

interface ApprovalDialogProps {
  property: any
  action: "approve" | "reject"
  open: boolean
  onOpenChange: (open: boolean) => void
  onConfirm: (reason?: string) => void
}

export function ApprovalDialog({ property, action, open, onOpenChange, onConfirm }: ApprovalDialogProps) {
  const [reason, setReason] = useState("")

  const handleConfirm = () => {
    onConfirm(action === "reject" ? reason : undefined)
    setReason("")
  }

  const isApprove = action === "approve"

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-md">
        <DialogHeader>
          <DialogTitle className="flex items-center space-x-2">
            {isApprove ? (
              <>
                <Check className="h-5 w-5 text-green-600" />
                <span>Duyệt bài đăng</span>
              </>
            ) : (
              <>
                <X className="h-5 w-5 text-red-600" />
                <span>Từ chối bài đăng</span>
              </>
            )}
          </DialogTitle>
          <DialogDescription>
            {isApprove ? "Bạn có chắc chắn muốn duyệt bài đăng này?" : "Vui lòng nhập lý do từ chối bài đăng này."}
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-4">
          {/* Property Info */}
          <div className="p-4 bg-muted rounded-lg">
            <h4 className="font-medium">{property.title}</h4>
            <p className="text-sm text-muted-foreground">Chủ trọ: {property.landlord}</p>
            <p className="text-sm text-muted-foreground">Giá: {(property.price / 1000000).toFixed(1)}M VND/tháng</p>
          </div>

          {/* Rejection Reason */}
          {!isApprove && (
            <div className="space-y-2">
              <Label htmlFor="reason">Lý do từ chối *</Label>
              <Textarea
                id="reason"
                placeholder="Nhập lý do từ chối bài đăng (ví dụ: hình ảnh không rõ ràng, thiếu thông tin...)"
                value={reason}
                onChange={(e) => setReason(e.target.value)}
                rows={4}
              />
            </div>
          )}

          {/* Actions */}
          <div className="flex justify-end space-x-2 pt-4">
            <Button variant="outline" onClick={() => onOpenChange(false)}>
              Hủy
            </Button>
            <Button
              onClick={handleConfirm}
              variant={isApprove ? "default" : "destructive"}
              disabled={!isApprove && !reason.trim()}
            >
              {isApprove ? "Duyệt tin" : "Từ chối"}
            </Button>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  )
}
