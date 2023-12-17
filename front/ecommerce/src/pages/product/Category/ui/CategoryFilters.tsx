import {Container, Title, CheckboxList, CheckboxItem} from '../styles/Category.styles';
import { CategoryFiltersProps } from '../types/Category.types';

const CategoryFilters: React.FC<CategoryFiltersProps> = ({ optionsData, priceFilters, onOptionChange, onPriceChange }) => {
    return (
        <div className="filters-container">
            <div className="option-filter-group">
                {optionsData.map(group => (
                    <Container key={group.optionId}>
                    <Title>{group.optionName}</Title>
                    <CheckboxList>
                        {group.optionVariationNames.map((variation, variationIndex) => (
                        <CheckboxItem key={`${group.optionId}-${variationIndex}`}>
                            <input 
                            type="checkbox" 
                            onChange={(e) => onOptionChange(group.optionId, variation, e.target.checked, e)}
                            />
                            <label>
                                {variation}
                            </label>
                        </CheckboxItem>
                        ))}
                    </CheckboxList>
                    </Container>
                ))}
            </div>

            <div className="price-filter-group">
                <Container key="price-key">
                    <Title>Price</Title>
                    {[{min: 0, max: 50000}, {min: 50000, max: 100000}, {min: 100000, max: 150000}, {min: 150000, max: 200000}].map((range, index) => (
                        <CheckboxList key={`price-range-${index}`}>
                            <CheckboxItem>
                                <input 
                                type="checkbox" 
                                checked={!!priceFilters.some(filter => filter.min === range.min && filter.max === range.max)}
                                onChange={() => onPriceChange({ min: range.min, max: range.max })}
                                />
                                <label>
                                    {`${range.min} - ${range.max} 원`}
                                </label>
                            </CheckboxItem>
                        </CheckboxList>
                    ))}
                </Container>
            </div>
        </div>
    );
};

export default CategoryFilters;
