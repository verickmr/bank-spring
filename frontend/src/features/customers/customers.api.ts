import { request } from '../../shared/api/httpClient'
import type { Customer } from './customers.types'

export function listCustomers() {
  return request<Customer[]>('/api/correntistas')
}
