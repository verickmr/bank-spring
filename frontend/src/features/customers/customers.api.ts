import { HttpError, request } from '../../shared/api/httpClient'
import type { CreateCustomerPayload, Customer } from './customers.types'

export async function getCurrentCustomer() {
  const customers = await request<Customer[]>('/api/correntistas')
  const customer = customers[0]

  if (!customer) {
    throw new HttpError('Correntista autenticado não encontrado.', 404)
  }

  return customer
}

export function createCustomer(payload: CreateCustomerPayload) {
  return request<Customer>('/api/correntistas', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}
