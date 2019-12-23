package org.wildcat.camada.service.impl;

import org.springframework.stereotype.Service;
import org.wildcat.camada.common.enumerations.CamadaQuery;
import org.wildcat.camada.persistence.PaymentFrequency;
import org.wildcat.camada.persistence.dto.PartnerDTO;
import org.wildcat.camada.persistence.entity.CustomQuery;
import org.wildcat.camada.persistence.entity.Partner;
import org.wildcat.camada.persistence.repository.PartnerRepository;
import org.wildcat.camada.service.PartnerService;
import org.wildcat.camada.service.utils.GetUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.wildcat.camada.common.enumerations.CamadaQuery.valueOf;

@Service
public class PartnerServiceImpl implements PartnerService {

    @Resource
    private final PartnerRepository partnerRepository;

    public PartnerServiceImpl(PartnerRepository partnerRepository) {
        this.partnerRepository = partnerRepository;
    }

    @Override
    public List<PartnerDTO> findAllByCustomQuery(CustomQuery customQuery) {
        List<PartnerDTO> partners = new ArrayList<>();
        CamadaQuery query = valueOf(customQuery.getQuery());
        switch (query) {
            case ALL_PARTNERS:
                partners = partnerRepository.findAllPartnersDTO();
                break;
            case NEXT_PAYMENTS_PARTNERS:
                partners = getNextPaymentsPartnersDTO();
                break;
            case NEW_PARTNERS:
                partners = null;
                break;
            case ALL_FORMER_PARTNERS:
                partners = null;
                break;
            case LAST_MONTH_FORMER_PARTNERS:
                partners = null;
                break;
        }
        return partners;
    }

    @Override
    public void delete(Long id) {
        partnerRepository.deleteById(id);
    }

    @Override
    public void saveEntity(Partner partner) {
        save(partner);
    }

    @Override
    public Partner find(Long id) {
        return partnerRepository.findById(id).orElse(null);
    }

    @Override
    public Partner save(Partner partner) {
        return partnerRepository.save(partner);
    }

    @Override
    public List<PartnerDTO> generateNextPayments() {
        List<PartnerDTO> nextPaymentsPartnersDTO = getNextPaymentsPartnersDTO();
        return nextPaymentsPartnersDTO.stream()
                .map(partnerDTO -> {
                    Double amount = GetUtils.get(() -> Double.parseDouble(partnerDTO.getAmount()), 0.0);
                    PaymentFrequency paymentFrequency = partnerDTO.getPaymentFrequency();
                    switch (paymentFrequency) {
                        case QUATERLY:
                            amount *= 3;
                            break;
                        case BIANNUAL:
                            amount *= 6;
                            break;
                        case YEARLY:
                            amount *= 12;
                            break;
                    }
                    partnerDTO.setAmount(Double.toString(amount));
                    return partnerDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<PartnerDTO> findByDni(String dni) {
        return partnerRepository.findPartnersDTOByDni(dni);
    }

    /**
     * Fetches active partners, an active partner is defined as the one that has been subscribed before the actual date
     * and hasn't been unsubscribed (subscribedTo field is null) or subscribedTo is greater than the actual date.
     * <p>
     * As we have different payment options (see PaymentFrequency), we need to calculate if the partner needs to pay in
     * the current date, therefore we need to use the information from subscribeFrom to calculate whether the partner needs
     * to pay this month or not. As we have 3 payment options (monthly, quaterly, yearly) this is pretty straightforward:
     * <p>
     * - Monthly payment partners pay every month (obviously)
     * - Quaterly pay each 3 months, calculate the next 3, 6 and 9 months in the current year from the subscribedFrom field
     * and check if the actual month matches in the list.
     * - Biannual pay each 6 months, calculate the next 6 months in the current year from the subscribedFrom field and check
     * if the actual month matches in the list.
     * - Yearly pay when the month is the same as the current one.
     */
    private List<PartnerDTO> getNextPaymentsPartnersDTO() {
        Set<PartnerDTO> selectedPartners = new HashSet<>();
        List<PartnerDTO> activePartners = partnerRepository.findActivePartnersDTO();
        Set<PartnerDTO> monthlyPartners = activePartners.stream()
                .filter((partner) -> PaymentFrequency.MONTHLY.equals(partner.getPaymentFrequency()))
                .collect(Collectors.toSet());
        Set<PartnerDTO> quaterlyPartners = activePartners.stream()
                .filter((partner) -> PaymentFrequency.QUATERLY.equals(partner.getPaymentFrequency()) && isPayMonth(partner, false))
                .collect(Collectors.toSet());
        Set<PartnerDTO> biannualPartners = activePartners.stream()
                .filter((partner) -> PaymentFrequency.BIANNUAL.equals(partner.getPaymentFrequency()) && isPayMonth(partner, true))
                .collect(Collectors.toSet());
        Set<PartnerDTO> yearlyPartners = activePartners.stream()
                .filter((partner) -> PaymentFrequency.YEARLY.equals(partner.getPaymentFrequency()) && isSameMonth(partner))
                .collect(Collectors.toSet());
        selectedPartners.addAll(monthlyPartners);
        selectedPartners.addAll(quaterlyPartners);
        selectedPartners.addAll(biannualPartners);
        selectedPartners.addAll(yearlyPartners);
        return new LinkedList<>(selectedPartners);
    }

    private boolean isSameMonth(PartnerDTO partnerDTO) {
        return GetUtils.get(() -> {
            return partnerDTO.getSubscribedFrom().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonthValue() == LocalDate.now().getMonthValue();
        }, false);
    }

    private boolean isPayMonth(PartnerDTO partnerDTO, boolean isBiannual) {
        int monthValue = GetUtils.get(() -> {
            return partnerDTO.getSubscribedFrom().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonthValue();
        }, 100);
        int incrementor = (isBiannual) ? 6 : 3;
        List<Integer> months = new ArrayList<>();
        for (int i = monthValue; i < 13; i += incrementor) {
            months.add(i);
        }
        int actualMonth = LocalDate.now().getMonthValue();
        return months.stream().anyMatch(month -> month.equals(actualMonth));
    }

}
