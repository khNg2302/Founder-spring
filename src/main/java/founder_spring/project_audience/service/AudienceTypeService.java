package founder_spring.project_audience.service;

import founder_spring.common.exception.ConflictException;
import founder_spring.common.exception.ResourceNotFoundException;
import founder_spring.common.util.CuidGenerator;
import founder_spring.project_audience.dto.AudienceTypeResponse;
import founder_spring.project_audience.dto.CreateAudienceTypeRequest;
import founder_spring.project_audience.dto.UpdateAudienceTypeRequest;
import founder_spring.project_audience.entity.AudienceType;
import founder_spring.project_audience.repository.AudienceTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AudienceTypeService {

    private final AudienceTypeRepository audienceTypeRepository;
    private final CuidGenerator cuidGenerator;

    @Transactional(readOnly = true)
    public List<AudienceTypeResponse> getAll() {
        return audienceTypeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AudienceTypeResponse getById(String id) {
        AudienceType audienceType = findById(id);

        return toResponse(audienceType);
    }

    @Transactional
    public AudienceTypeResponse create(CreateAudienceTypeRequest request) {

        String name = request.getName().trim();

        if (audienceTypeRepository.existsByNameIgnoreCase(name)) {
            throw new ConflictException(
                    "Audience type with this name already exists"
            );
        }

        AudienceType audienceType = new AudienceType();

        audienceType.setId(cuidGenerator.generate());
        audienceType.setName(name);
        audienceType.setDescription(request.getDescription());
        audienceType.setActive(true);

        audienceTypeRepository.save(audienceType);

        return toResponse(audienceType);
    }

    @Transactional
    public AudienceTypeResponse update(
            String id,
            UpdateAudienceTypeRequest request
    ) {
        AudienceType audienceType = findById(id);

        String name = request.getName().trim();

        if (!audienceType.getName().equalsIgnoreCase(name)
                && audienceTypeRepository.existsByNameIgnoreCase(name)) {
            throw new ConflictException(
                    "Audience type with this name already exists"
            );
        }

        audienceType.setName(name);
        audienceType.setDescription(request.getDescription());

        return toResponse(audienceTypeRepository.save(audienceType));
    }

    @Transactional
    public void updateActive(String id, boolean active) {

        AudienceType audienceType = findById(id);

        audienceType.setActive(active);

        audienceTypeRepository.save(audienceType);
    }

    private AudienceType findById(String id) {
        return audienceTypeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Audience type not found"
                        )
                );
    }

    private AudienceTypeResponse toResponse(AudienceType audienceType) {
        return AudienceTypeResponse.builder()
                .id(audienceType.getId())
                .name(audienceType.getName())
                .description(audienceType.getDescription())
                .active(audienceType.getActive())
                .createdAt(audienceType.getCreatedAt())
                .updatedAt(audienceType.getUpdatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<AudienceTypeResponse> getActiveAudienceTypes() {
        return audienceTypeRepository
                .findAllByActiveTrueOrderByNameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }
}