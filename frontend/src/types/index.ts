export interface User {
    id?: string;
    email: string;
    username: string;
    role: 'OWNER' | 'MANAGER' | 'TENANT';
}

export interface Apartment {
    id: string;
    name: string;
    address: string;
    totalFloors: number;
    totalFlats: number;
    managerId: string;  // Reference to User (Manager)
    ownerId: string;    // Reference to User (Owner)
    status: 'ACTIVE' | 'INACTIVE';
}

export interface Flat {
    id: string;
    flatNumber: string;
    floor: number;
    area: number;
    rent: number;
    type: 'ONE_BHK' | 'TWO_BHK' | 'THREE_BHK' | 'FOUR_BHK';
    status: 'VACANT' | 'OCCUPIED' | 'MAINTENANCE';
}

export interface Tenant {
    id: string;
    user: {
        id: string;
        username: string;
        email: string;
    };
    flat: {
        id: string;
        flatNumber: string;
    };
    leaseStart: string;
    leaseEnd: string;
    phone: string;
}

export interface Payment {
    id: string;
    tenantId: string;
    flatId: string;
    amount: number;
    type: 'RENT' | 'DEPOSIT' | 'MAINTENANCE' | 'OTHER';
    status: 'PENDING' | 'COMPLETED' | 'OVERDUE';
    dueDate: string;
    paidDate?: string;
    description?: string;
}

export interface Complaint {
    id: string;
    tenantId: string;
    flatId: string;
    title: string;
    description: string;
    status: 'OPEN' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED';
    priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
    createdAt: string;
    resolvedAt?: string;
    assignedTo?: string;  // Reference to User (Manager)
}

export interface Document {
    id: string;
    tenantId: string;
    title: string;
    description: string;
    type: 'LEASE' | 'ID_PROOF' | 'INVOICE' | 'OTHER';
    fileUrl: string;
    uploadedAt: string;
    verified: boolean;
}

export interface AuthResponse {
    token: string;
    type: string;
    email: string;
    role: string;
} 