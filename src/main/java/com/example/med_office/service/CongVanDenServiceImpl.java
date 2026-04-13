package com.example.med_office.service;

import com.example.med_office.dto.ApiCode;
import com.example.med_office.dto.CongVanDenCreateRequest;
import com.example.med_office.dto.CongVanDenResponse;
import com.example.med_office.entity.CongVanDen;
import com.example.med_office.entity.NhaCungCap;
import com.example.med_office.repository.CongVanDenRepository;
import com.example.med_office.repository.NhaCungCapRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CongVanDenServiceImpl implements CongVanDenService {

    private final CongVanDenRepository congVanDenRepository;
    private final NhaCungCapRepository nhaCungCapRepository;

    public CongVanDenServiceImpl(CongVanDenRepository congVanDenRepository, NhaCungCapRepository nhaCungCapRepository) {
        this.congVanDenRepository = congVanDenRepository;
        this.nhaCungCapRepository = nhaCungCapRepository;
    }

    @Override
    public CongVanDenResponse create(CongVanDenCreateRequest request) {
        if (congVanDenRepository.existsBySoCongVanIgnoreCase(request.getSoCongVan().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "So cong van da ton tai");
        }

        NhaCungCap nhaCungCap = nhaCungCapRepository.findById(request.getDonViGuiId().longValue())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Don vi gui khong ton tai"));

        CongVanDen entity = new CongVanDen();
        entity.setSoCongVan(request.getSoCongVan().trim());
        entity.setSoDen(request.getSoDen());
        entity.setTieuDe(request.getTieuDe());
        entity.setNoiDungTomTat(request.getNoiDungTomTat());
        entity.setDonViGuiId(request.getDonViGuiId());
        entity.setDonViGui(nhaCungCap.getTenNhaCungCap());
        entity.setNguoiKy(request.getNguoiKy());
        entity.setNgayVanBan(request.getNgayVanBan());
        entity.setNgayNhan(request.getNgayNhan());
        entity.setMucDoKhan(request.getMucDoKhan());
        entity.setMucDoMat(request.getMucDoMat());
        entity.setNguonNhan(request.getNguonNhan());

        CongVanDen saved = congVanDenRepository.save(entity);
        return new CongVanDenResponse(
                saved.getId(),
                saved.getSoCongVan(),
                saved.getSoDen(),
                saved.getTieuDe(),
                saved.getNoiDungTomTat(),
                saved.getDonViGuiId(),
                saved.getDonViGui(),
                saved.getNguoiKy(),
                saved.getNgayVanBan(),
                saved.getNgayNhan(),
                saved.getMucDoKhan(),
                saved.getMucDoMat(),
                saved.getNguonNhan(),
                saved.getTrangThai(),
                saved.getNgayTao()
        );
    }
}
