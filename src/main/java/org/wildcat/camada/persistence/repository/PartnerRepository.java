package org.wildcat.camada.persistence.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.wildcat.camada.persistence.entity.Partner;
import org.wildcat.camada.persistence.dto.PartnerView;

import java.util.List;

@Repository
public interface PartnerRepository extends CrudRepository<Partner, Long> {
    @Query(value = "select new org.wildcat.camada.persistence.dto.PartnerView(" +
            "   p.id, p.amount, p.paymentFrequency, p.camadaId, " +
            "   pd.id, pd.dni, pd.name, pd.surnames, pd.birthDate, " +
            "   pd.address, pd.location, pd.province, pd.postCode, pd.phone1, pd.phone2, pd.email, " +
            "   bd.id, bd.iban, bd.name, bd.surnames) " +
            "from Partner p " +
            "    inner join p.bankingData bd " +
            "    inner join p.personalData pd ")
    List<PartnerView> findAllPartners();
}
