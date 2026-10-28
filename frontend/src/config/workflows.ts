import type { WorkflowAction } from "../components/workflows/WorkflowPanel";
export const workflows: Record<
  string,
  { title: string; path: string; roles: string[]; actions: WorkflowAction[] }[]
> = {
  staff: [
    {
      title: "Departments",
      path: "/departments",
      roles: ["ADMIN", "RECEPTIONIST", "DOCTOR", "NURSE"],
      actions: [
        {
          title: "Add department",
          path: "/departments",
          roles: ["ADMIN"],
          fields: [
            {
              key: "name",
              label: "Name",
            },
          ],
          row: false,
          method: "post",
        },
        {
          title: "Edit",
          path: "/departments/{id}",
          roles: ["ADMIN"],
          fields: [
            {
              key: "name",
              label: "Name",
            },
            {
              key: "status",
              label: "Status",
              options: ["ACTIVE", "INACTIVE"],
            },
          ],
          row: true,
          method: "put",
        },
      ],
    },
    {
      title: "Working schedules",
      path: "/staff/schedules",
      roles: ["ADMIN", "DOCTOR", "RECEPTIONIST"],
      actions: [
        {
          title: "Add schedule",
          path: "/staff/schedules",
          roles: ["ADMIN", "DOCTOR"],
          fields: [
            {
              key: "staffId",
              label: "Staff member",
              lookup: "/staff",
            },
            {
              key: "dayOfWeek",
              label: "Day",
              options: [
                "MONDAY",
                "TUESDAY",
                "WEDNESDAY",
                "THURSDAY",
                "FRIDAY",
                "SATURDAY",
                "SUNDAY",
              ],
            },
            {
              key: "startsAt",
              label: "Start time",
              type: "time",
            },
            {
              key: "endsAt",
              label: "End time",
              type: "time",
            },
          ],
          row: false,
          method: "post",
        },
      ],
    },
    {
      title: "Staff leave",
      path: "/staff/leave",
      roles: ["ADMIN", "DOCTOR", "NURSE", "RECEPTIONIST"],
      actions: [
        {
          title: "Record leave",
          path: "/staff/leave",
          roles: ["ADMIN", "DOCTOR", "NURSE"],
          fields: [
            {
              key: "staffId",
              label: "Staff member",
              lookup: "/staff",
            },
            {
              key: "startsOn",
              label: "From",
              type: "date",
            },
            {
              key: "endsOn",
              label: "Until",
              type: "date",
            },
            {
              key: "reason",
              label: "Reason",
              type: "textarea",
              optional: true,
            },
          ],
          row: false,
          method: "post",
        },
        {
          title: "Decide",
          path: "/staff/leave/{id}/decision",
          roles: ["ADMIN"],
          row: true,
          fields: [
            {
              key: "status",
              label: "Decision",
              options: ["APPROVED", "REJECTED"],
            },
          ],
        },
      ],
    },
  ],
  lab: [
    {
      title: "Test catalogue & reference ranges",
      path: "/lab/catalog",
      roles: ["ADMIN", "DOCTOR", "LAB_TECHNICIAN"],
      actions: [
        {
          title: "Add test",
          path: "/lab/catalog",
          roles: ["ADMIN", "LAB_TECHNICIAN"],
          fields: [
            {
              key: "name",
              label: "Name",
            },
            {
              key: "referenceRange",
              label: "Reference interval",
            },
            {
              key: "unit",
              label: "Unit",
            },
            {
              key: "specimenType",
              label: "Specimen type",
              optional: true,
            },
            {
              key: "turnaroundHours",
              label: "Turnaround hours",
              type: "number",
              min: 1,
              optional: true,
            },
            {
              key: "price",
              label: "Price",
              type: "number",
              min: 0,
              optional: true,
            },
          ],
          row: false,
          method: "post",
        },
        {
          title: "Edit",
          path: "/lab/catalog/{id}",
          roles: ["ADMIN", "LAB_TECHNICIAN"],
          fields: [
            {
              key: "name",
              label: "Name",
            },
            {
              key: "referenceRange",
              label: "Reference interval",
            },
            {
              key: "unit",
              label: "Unit",
            },
            {
              key: "status",
              label: "Status",
              options: ["ACTIVE", "INACTIVE"],
            },
            {
              key: "specimenType",
              label: "Specimen type",
              optional: true,
            },
            {
              key: "turnaroundHours",
              label: "Turnaround hours",
              type: "number",
              min: 1,
              optional: true,
            },
            {
              key: "price",
              label: "Price",
              type: "number",
              min: 0,
              optional: true,
            },
          ],
          row: true,
          method: "put",
        },
      ],
    },
  ],
  pharmacy: [
    {
      title: "Medicine catalogue",
      path: "/pharmacy/catalog",
      roles: ["ADMIN", "DOCTOR", "PHARMACIST"],
      actions: [
        {
          title: "Add medicine",
          path: "/pharmacy/catalog",
          roles: ["ADMIN", "PHARMACIST"],
          fields: [
            {
              key: "name",
              label: "Name",
            },
            {
              key: "strength",
              label: "Strength",
            },
            {
              key: "unit",
              label: "Unit",
            },
            {
              key: "reorderLevel",
              label: "Reorder threshold",
              type: "number",
              min: 0,
              optional: true,
            },
            {
              key: "manufacturer",
              label: "Manufacturer",
              optional: true,
            },
          ],
          row: false,
          method: "post",
        },
        {
          title: "Edit",
          path: "/pharmacy/catalog/{id}",
          roles: ["ADMIN", "PHARMACIST"],
          fields: [
            {
              key: "name",
              label: "Name",
            },
            {
              key: "strength",
              label: "Strength",
            },
            {
              key: "unit",
              label: "Unit",
            },
            {
              key: "status",
              label: "Status",
              options: ["ACTIVE", "INACTIVE"],
            },
            {
              key: "reorderLevel",
              label: "Reorder threshold",
              type: "number",
              min: 0,
              optional: true,
            },
            {
              key: "manufacturer",
              label: "Manufacturer",
              optional: true,
            },
          ],
          row: true,
          method: "put",
        },
      ],
    },
    {
      title: "Low stock & expiry alerts",
      path: "/pharmacy/alerts",
      roles: ["ADMIN", "PHARMACIST"],
      actions: [],
    },
    {
      title: "Inventory movement history",
      path: "/pharmacy/movements",
      roles: ["ADMIN", "PHARMACIST"],
      actions: [],
    },
  ],
  billing: [
    {
      title: "Outstanding balances",
      path: "/billing/balances",
      roles: ["ADMIN", "BILLING_STAFF"],
      actions: [],
    },
    {
      title: "Payment history",
      path: "/billing/payments",
      roles: ["ADMIN", "BILLING_STAFF"],
      actions: [],
    },
    {
      title: "Refund workflow",
      path: "/billing/refunds",
      roles: ["ADMIN", "BILLING_STAFF"],
      actions: [
        {
          title: "Approve refund",
          path: "/billing/refunds/{id}/approve",
          roles: ["ADMIN"],
          fields: [],
          row: true,
          method: "post",
        },
        {
          title: "Reject",
          path: "/billing/refunds/{id}/reject",
          roles: ["ADMIN"],
          row: true,
          fields: [
            {
              key: "reason",
              label: "Reason",
            },
          ],
        },
        {
          title: "Settle refund",
          path: "/billing/refunds/{id}/settle",
          roles: ["BILLING_STAFF"],
          row: true,
          fields: [],
        },
      ],
    },
  ],
  notifications: [
    {
      title: "Failed email delivery",
      path: "/notifications/failed",
      roles: ["ADMIN"],
      actions: [
        {
          title: "Retry delivery",
          path: "/notifications/failed/{id}/retry",
          roles: ["ADMIN"],
          fields: [],
          row: true,
          method: "post",
        },
      ],
    },
  ],
};
