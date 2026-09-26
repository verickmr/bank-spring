import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from 'react-router'
import { HttpError } from '../../shared/api/httpClient'
import { createCustomer } from './customers.api'
import type { CustomerCreateFormData } from './customers.schema'
import type { Customer } from './customers.types'

export function useCreateCustomer() {
  const navigate = useNavigate()
  const queryClient = useQueryClient()

  return useMutation<Customer, HttpError, CustomerCreateFormData>({
    mutationFn: createCustomer,
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['customers'] })
      navigate('/correntistas', {
        replace: true,
        state: { customerCreated: true },
      })
    },
  })
}
