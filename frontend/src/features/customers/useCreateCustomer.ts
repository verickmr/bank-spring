import { useMutation } from '@tanstack/react-query'
import { useNavigate } from 'react-router'
import { HttpError } from '../../shared/api/httpClient'
import { createCustomer } from './customers.api'
import type { CustomerCreateFormData } from './customers.schema'
import type { Customer } from './customers.types'

type CreateCustomerNavigation = {
  redirectTo: string
  state?: Record<string, boolean>
}

export function useCreateCustomer({ redirectTo, state }: CreateCustomerNavigation) {
  const navigate = useNavigate()

  return useMutation<Customer, HttpError, CustomerCreateFormData>({
    mutationFn: createCustomer,
    onSuccess: () => {
      navigate(redirectTo, { replace: true, state })
    },
  })
}
