# Telecom Offer Recommendation Policy - Simplified

## Purpose
Recommend telecom products to customers while ensuring regulatory compliance, transparency, and prioritizing eligible offers based on campaigns and operational constraints.

## How It Works - 3 Evaluation Phases

### Phase 1: Eligibility Check
An offer is **EXCLUDED** if any of these conditions are true:
- Account is suspended
- Customer under 18 AND offer is regulated
- Offer is regulated AND verification is missing
- Customer's country is not in the allowed countries list
- High credit risk AND contract duration ≥ 12 months

### Phase 2: Campaign & Priority
- **BackToSchool Campaign**: Increases visibility for all customer segments
- **SummerBoost Campaign**: Increases visibility for matching offers
- **Standard**: Normal priority if no campaign applies

### Phase 3: Inventory Check
- **Low Stock** → Offer excluded
- **Medium Stock** → Normal priority
- **High Stock** → High priority (promotion capability)

## Final Recommendation
- **High Priority**: Eligible + High stock OR campaign boost applied
- **Normal Priority**: Eligible + Medium stock + No campaign boost
- **Excluded**: Failed eligibility OR low stock

## Output
Each evaluation provides:
- Recommended offers list
- Excluded offers list
- Priority level per offer
- Reason codes (e.g., ACCOUNT_SUSPENDED, LOW_STOCK, CAMPAIGN_BOOST)

## Example
**Student (age 20, Netherlands, Active account, BackToSchool campaign)**
- T-100 (Student 5G) → High Priority (campaign + high stock)
- T-200 (Smartwatch) → Normal Priority
- T-300 (Business SIM) → High Priority
- T-400 (Travel Pack) → Excluded (low stock)
- T-500 (Premium 5G) → High Priority

## Governance
- Operations maintains stock thresholds
- Commercial teams maintain contract rules
- All changes must be versioned for auditability

## Reference
**Policy Version**: 2.1 

**Original Policy**: [telecom-product-recommendation-201.md](https://github.com/DecisionsDev/decision-assistant-hub/blob/main/examples/product-recommendation/telecom-product-recommendation-201.md)